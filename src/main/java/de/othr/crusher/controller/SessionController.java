package de.othr.crusher.controller;

import de.othr.crusher.model.GoEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.SessionEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.ProjectRepository;
import de.othr.crusher.repository.SectorRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

/**
 * Controller for managing climbing sessions. Provides endpoints for viewing sessions, creating new
 * sessions, and ending active sessions.
 */
@Controller
public class SessionController {

  private final SessionRepository sessionRepository;
  private final UserRepository userRepository;
  private final GymRepository gymRepository;
  private final GoRepository goRepository;
  private final BoulderRepository boulderRepository;
  private final SectorRepository sectorRepository;
  private final GradeRepository gradeRepository;
  private final ProjectRepository projectRepository;

  public SessionController(
      SessionRepository sessionRepository,
      UserRepository userRepository,
      GymRepository gymRepository,
      GoRepository goRepository,
      BoulderRepository boulderRepository,
      SectorRepository sectorRepository,
      GradeRepository gradeRepository,
      ProjectRepository projectRepository) {
    this.sessionRepository = sessionRepository;
    this.userRepository = userRepository;
    this.gymRepository = gymRepository;
    this.goRepository = goRepository;
    this.boulderRepository = boulderRepository;
    this.sectorRepository = sectorRepository;
    this.gradeRepository = gradeRepository;
    this.projectRepository = projectRepository;
  }

  /**
   * Displays all sessions for the current user.
   *
   * @param page current page number (1-indexed, defaults to 1)
   * @param principal the authenticated user
   * @param model Spring model to pass data to the view
   * @return view name for the sessions list page
   */
  @GetMapping("/sessions")
  @Transactional(readOnly = true)
  public String showAllSessions(
      @RequestParam(value = "page", defaultValue = "1") int page,
      Principal principal,
      Model model) {
    UserEntity user = findUserByPrincipal(principal);

    // Convert 1-based page to 0-based for Spring's PageRequest
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<SessionEntity> sessionsPage =
        sessionRepository.findByUserIdOrderByStartedAtDesc(user.getId(), pageable);
    List<SessionEntity> sessions = sessionsPage.getContent();

    // Find the most recently used gym
    GymEntity lastGym =
        sessions.stream()
            .filter(session -> session.getGym() != null)
            .findFirst()
            .map(SessionEntity::getGym)
            .orElse(null);

    // Find active session (if any)
    SessionEntity activeSession =
        sessions.stream().filter(session -> session.getEndedAt() == null).findFirst().orElse(null);

    model.addAttribute("sessions", sessions);
    model.addAttribute("sessionsPage", sessionsPage);
    model.addAttribute("lastGym", lastGym);
    model.addAttribute("activeSession", activeSession);

    return "pages/sessions/all";
  }

  /**
   * Displays the form for creating a new session (gym selection).
   *
   * @param model Spring model to pass data to the view
   * @return view name for the session creation page
   */
  @GetMapping("/sessions/create")
  public String showCreateForm(Model model) {
    List<GymEntity> gyms = gymRepository.findByDeletedFalse();
    model.addAttribute("gyms", gyms);

    return "pages/sessions/create";
  }

  /**
   * Creates a new session for the current user at the selected gym. If an active session already
   * exists, shows an error message.
   *
   * @param gymId the ID of the selected gym
   * @param principal the authenticated user
   * @param redirectAttributes attributes for flash messages on redirect
   * @return redirect to the newly created session detail page or back with error
   */
  @PostMapping("/sessions")
  public String createSession(
      @RequestParam("gymId") Long gymId,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    UserEntity user = findUserByPrincipal(principal);
    GymEntity gym =
        gymRepository
            .findByIdAndDeletedFalse(gymId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

    // Check for active sessions
    List<SessionEntity> activeSessions =
        sessionRepository.findByUserIdOrderByStartedAtDesc(user.getId()).stream()
            .filter(session -> session.getEndedAt() == null)
            .toList();

    // If active session exists, show error message and redirect to active session
    if (!activeSessions.isEmpty()) {
      SessionEntity activeSession = activeSessions.get(0);
      redirectAttributes.addFlashAttribute(
          "toast",
          Map.of(
              "type",
              "error",
              "message",
              "You already have an active session at "
                  + activeSession.getGym().getName()
                  + ". Please end it before starting a new one."));
      return "redirect:/sessions/" + activeSession.getId();
    }

    // No active session, create new one
    SessionEntity session = new SessionEntity();
    session.setStartedAt(LocalDateTime.now());
    session.setUser(user);
    session.setGym(gym);

    SessionEntity savedSession = sessionRepository.save(session);

    // Add success message for toast notification
    redirectAttributes.addFlashAttribute(
        "toast",
        Map.of(
            "type", "success",
            "message", "Session created successfully!"));

    return "redirect:/sessions/" + savedSession.getId();
  }

  /**
   * Displays details for a specific session.
   *
   * @param id session ID
   * @param page current page number (1-indexed, defaults to 1)
   * @param principal the authenticated user
   * @param model Spring model to pass data to the view
   * @return view name for the session detail page
   */
  @GetMapping("/sessions/{id}")
  @Transactional(readOnly = true)
  public String showSession(
      @PathVariable("id") Long id,
      @RequestParam(value = "page", defaultValue = "1") int page,
      Principal principal,
      Model model) {
    UserEntity user = findUserByPrincipal(principal);
    SessionEntity session =
        sessionRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

    // Ensure the session belongs to the current user
    if (!session.getUser().getId().equals(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    // Convert 1-based page to 0-based for Spring's PageRequest
    Pageable pageable = PageRequest.of(page - 1, 10);
    Page<GoEntity> goesPage =
        goRepository.findBySessionIdOrderByTimestampDesc(session.getId(), pageable);

    model.addAttribute("currentSession", session);
    model.addAttribute("goesPage", goesPage);
    model.addAttribute("goes", goesPage.getContent());

    return "pages/sessions/detail";
  }

  /**
   * Ends an active session by setting its end time.
   *
   * @param id session ID
   * @param principal the authenticated user
   * @param redirectAttributes attributes for flash messages on redirect
   * @return redirect to the sessions list page
   */
  @PostMapping("/sessions/{id}/end")
  @Transactional
  public String endSession(
      @PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
    UserEntity user = findUserByPrincipal(principal);
    SessionEntity session =
        sessionRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

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
    redirectAttributes.addFlashAttribute(
        "toast",
        Map.of(
            "type", "success",
            "message", "Session ended successfully!"));

    return "redirect:/sessions";
  }

  /**
   * Deletes a session.
   *
   * @param id session ID
   * @param principal the authenticated user
   * @param redirectAttributes attributes for flash messages on redirect
   * @return redirect to the sessions list page
   */
  @DeleteMapping("/sessions/{id}")
  @Transactional
  public String deleteSession(
      @PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
    UserEntity user = findUserByPrincipal(principal);
    SessionEntity session =
        sessionRepository
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

    // Ensure the session belongs to the current user
    if (!session.getUser().getId().equals(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    sessionRepository.delete(session);

    // Add success message for toast notification
    redirectAttributes.addFlashAttribute(
        "toast",
        Map.of(
            "type", "success",
            "message", "Session deleted successfully!"));

    return "redirect:/sessions";
  }

  /**
   * Helper method to find the current user from the security principal.
   *
   * @param principal the authenticated user principal
   * @return the UserEntity for the current user
   * @throws ResponseStatusException if the user is not found
   */
  private UserEntity findUserByPrincipal(Principal principal) {
    return userRepository
        .findByName(principal.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }
}
