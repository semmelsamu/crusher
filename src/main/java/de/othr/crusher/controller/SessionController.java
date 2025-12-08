package de.othr.crusher.controller;

import de.othr.crusher.model.GoEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.SessionEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing climbing sessions.
 * Provides endpoints for viewing sessions, creating new sessions, and ending active sessions.
 */
@Controller
public class SessionController {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GoRepository goRepository;

    public SessionController(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            GymRepository gymRepository,
            GoRepository goRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.gymRepository = gymRepository;
        this.goRepository = goRepository;
    }

    /**
     * Displays the dashboard with all sessions for the current user.
     *
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the dashboard page
     */
    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String showDashboard(Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);
        List<SessionEntity> sessions = sessionRepository.findByUserIdOrderByStartedAtDesc(user.getId());

        model.addAttribute("sessions", sessions);
        model.addAttribute("user", user);
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard")
        ));

        return "pages/dashboard";
    }

    /**
     * Displays the form for creating a new session (gym selection).
     *
     * @param model Spring model to pass data to the view
     * @return view name for the session creation page
     */
    @GetMapping("/sessions/create")
    public String showCreateForm(Model model) {
        List<GymEntity> gyms = gymRepository.findAll();
        model.addAttribute("gyms", gyms);
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard"),
                Map.of("label", "Start Session", "url", "/sessions/create")
        ));

        return "pages/sessions/create";
    }

    /**
     * Creates a new session for the current user at the selected gym.
     *
     * @param gymId the ID of the selected gym
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the newly created session detail page
     */
    @PostMapping("/sessions")
    public String createSession(@RequestParam("gymId") Long gymId, Principal principal, RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        GymEntity gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

        SessionEntity session = new SessionEntity();
        session.setStartedAt(LocalDateTime.now());
        session.setUser(user);
        session.setGym(gym);

        SessionEntity savedSession = sessionRepository.save(session);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Session created successfully!"
        ));

        return "redirect:/sessions/" + savedSession.getId();
    }

    /**
     * Displays details for a specific session.
     *
     * @param id session ID
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the session detail page
     */
    @GetMapping("/sessions/{id}")
    @Transactional(readOnly = true)
    public String showSession(@PathVariable("id") Long id, Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        // Ensure the session belongs to the current user
        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        List<GoEntity> goes = goRepository.findBySessionIdOrderByTimestampDesc(session.getId());

        model.addAttribute("currentSession", session);
        model.addAttribute("goes", goes);
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard"),
                Map.of("label", "Session", "url", "/sessions/" + session.getId())
        ));

        return "pages/sessions/detail";
    }

    /**
     * Ends an active session by setting its end time.
     *
     * @param id session ID
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the session detail page
     */
    @PostMapping("/sessions/{id}/end")
    @Transactional
    public String endSession(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        // Ensure the session belongs to the current user
        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        // Ensure the session is still running
        if (session.getEndedAt() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session already ended");
        }

        session.setEndedAt(LocalDateTime.now());
        sessionRepository.save(session);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Session ended successfully!"
        ));

        return "redirect:/sessions/" + id;
    }

    /**
     * Deletes a session.
     *
     * @param id session ID
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the dashboard
     */
    @DeleteMapping("/sessions/{id}")
    @Transactional
    public String deleteSession(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = sessionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        // Ensure the session belongs to the current user
        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        sessionRepository.delete(session);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Session deleted successfully!"
        ));

        return "redirect:/dashboard";
    }

    /**
     * Helper method to find the current user from the security principal.
     *
     * @param principal the authenticated user principal
     * @return the UserEntity for the current user
     * @throws ResponseStatusException if the user is not found
     */
    private UserEntity findUserByPrincipal(Principal principal) {
        return userRepository.findByName(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}

