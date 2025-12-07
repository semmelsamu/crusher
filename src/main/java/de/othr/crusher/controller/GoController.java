package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.GoEntity;
import de.othr.crusher.model.GoResult;
import de.othr.crusher.model.SessionEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing climbing attempts (gos) within a session.
 * <p>
 * Provides CRUD operations for tracking boulder attempts during a climbing session.
 * All operations are nested under the session resource path.
 * </p>
 */
@Controller
@RequestMapping("/sessions/{sessionId}/gos")
public class GoController {

    private final GoRepository goRepository;
    private final SessionRepository sessionRepository;
    private final BoulderRepository boulderRepository;
    private final UserRepository userRepository;

    public GoController(
            GoRepository goRepository,
            SessionRepository sessionRepository,
            BoulderRepository boulderRepository,
            UserRepository userRepository) {
        this.goRepository = goRepository;
        this.sessionRepository = sessionRepository;
        this.boulderRepository = boulderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Displays the form for creating a new go in a session.
     *
     * @param sessionId identifier of the parent session
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the go creation form
     */
    @GetMapping("/create")
    @Transactional(readOnly = true)
    public String showCreateForm(
            @PathVariable("sessionId") Long sessionId,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);

        // Get all boulders from the session's gym
        List<BoulderEntity> availableBoulders = boulderRepository.findBySectorGymId(session.getGym().getId());

        GoEntity go = new GoEntity();
        go.setSession(session);
        go.setTimestamp(LocalDateTime.now());

        model.addAttribute("currentSession", session);
        model.addAttribute("go", go);
        model.addAttribute("availableBoulders", availableBoulders);
        model.addAttribute("availableResults", GoResult.values());
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard"),
                Map.of("label", "Session", "url", "/sessions/" + sessionId),
                Map.of("label", "Record Go", "url", "")
        ));

        return "pages/gos/create";
    }

    /**
     * Displays the form for editing an existing go.
     *
     * @param sessionId identifier of the parent session
     * @param goId identifier of the go
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the go edit form
     */
    @GetMapping("/{goId}/edit")
    @Transactional(readOnly = true)
    public String showEditForm(
            @PathVariable("sessionId") Long sessionId,
            @PathVariable("goId") Long goId,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);
        GoEntity go = findGoInSessionOrThrow(sessionId, goId);

        List<BoulderEntity> availableBoulders = boulderRepository.findBySectorGymId(session.getGym().getId());

        model.addAttribute("currentSession", session);
        model.addAttribute("go", go);
        model.addAttribute("availableBoulders", availableBoulders);
        model.addAttribute("availableResults", GoResult.values());
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard"),
                Map.of("label", "Session", "url", "/sessions/" + sessionId),
                Map.of("label", "Go #" + goId, "url", "/sessions/" + sessionId + "/gos/" + goId),
                Map.of("label", "Edit", "url", "")
        ));

        return "pages/gos/edit";
    }

    /**
     * Creates a new go for the given session.
     *
     * @param sessionId identifier of the parent session
     * @param go go payload from the form
     * @param result validation result
     * @param principal the authenticated user
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the session detail page or back to the form on validation errors
     */
    @PostMapping
    @Transactional
    public String createGo(
            @PathVariable("sessionId") Long sessionId,
            @Valid @ModelAttribute("go") GoEntity go,
            BindingResult result,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);

        if (result.hasErrors()) {
            List<BoulderEntity> availableBoulders = boulderRepository.findBySectorGymId(session.getGym().getId());
            model.addAttribute("currentSession", session);
            model.addAttribute("go", go);
            model.addAttribute("availableBoulders", availableBoulders);
            model.addAttribute("availableResults", GoResult.values());
            model.addAttribute("breadcrumb", List.of(
                    Map.of("label", "Home", "url", "/"),
                    Map.of("label", "Dashboard", "url", "/dashboard"),
                    Map.of("label", "Session", "url", "/sessions/" + sessionId),
                    Map.of("label", "Record Go", "url", "")
            ));
            return "pages/gos/create";
        }

        // Validate that the boulder exists
        if (go.getBoulder() != null && go.getBoulder().getId() != null) {
            BoulderEntity boulder = boulderRepository.findById(go.getBoulder().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid boulder"));
            go.setBoulder(boulder);
        }

        go.setSession(session);
        if (go.getTimestamp() == null) {
            go.setTimestamp(LocalDateTime.now());
        }
        goRepository.save(go);

        return "redirect:/sessions/" + sessionId;
    }

    /**
     * Updates an existing go.
     *
     * @param sessionId identifier of the parent session
     * @param goId identifier of the go
     * @param formGo go payload from the form
     * @param result validation result
     * @param principal the authenticated user
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the session detail page or back to edit when validation errors occur
     */
    @PutMapping("/{goId}")
    @Transactional
    public String updateGo(
            @PathVariable("sessionId") Long sessionId,
            @PathVariable("goId") Long goId,
            @Valid @ModelAttribute("go") GoEntity formGo,
            BindingResult result,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);
        GoEntity go = findGoInSessionOrThrow(sessionId, goId);

        if (result.hasErrors()) {
            List<BoulderEntity> availableBoulders = boulderRepository.findBySectorGymId(session.getGym().getId());
            formGo.setId(go.getId());
            formGo.setSession(session);
            model.addAttribute("currentSession", session);
            model.addAttribute("go", formGo);
            model.addAttribute("availableBoulders", availableBoulders);
            model.addAttribute("availableResults", GoResult.values());
            model.addAttribute("breadcrumb", List.of(
                    Map.of("label", "Home", "url", "/"),
                    Map.of("label", "Dashboard", "url", "/dashboard"),
                    Map.of("label", "Session", "url", "/sessions/" + sessionId),
                    Map.of("label", "Go #" + goId, "url", "/sessions/" + sessionId + "/gos/" + goId),
                    Map.of("label", "Edit", "url", "")
            ));
            return "pages/gos/edit";
        }

        // Validate that the boulder exists
        if (formGo.getBoulder() != null && formGo.getBoulder().getId() != null) {
            BoulderEntity boulder = boulderRepository.findById(formGo.getBoulder().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid boulder"));
            go.setBoulder(boulder);
        }

        go.setResult(formGo.getResult());
        if (formGo.getTimestamp() != null) {
            go.setTimestamp(formGo.getTimestamp());
        }
        goRepository.save(go);

        return "redirect:/sessions/" + sessionId;
    }

    /**
     * Deletes an existing go.
     *
     * @param sessionId identifier of the parent session
     * @param goId identifier of the go
     * @param principal the authenticated user
     * @return redirect to the session detail page
     */
    @DeleteMapping("/{goId}")
    @Transactional
    public String deleteGo(
            @PathVariable("sessionId") Long sessionId,
            @PathVariable("goId") Long goId,
            Principal principal) {
        UserEntity user = findUserByPrincipal(principal);
        findSessionAndVerifyOwnership(sessionId, user);
        GoEntity go = findGoInSessionOrThrow(sessionId, goId);

        goRepository.delete(go);

        return "redirect:/sessions/" + sessionId;
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

    /**
     * Helper method to find a session and verify it belongs to the current user.
     *
     * @param sessionId the session identifier
     * @param user the current user
     * @return the SessionEntity
     * @throws ResponseStatusException if the session is not found or doesn't belong to the user
     */
    private SessionEntity findSessionAndVerifyOwnership(Long sessionId, UserEntity user) {
        SessionEntity session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

        if (!session.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        return session;
    }

    /**
     * Helper method to find a go and verify it belongs to the specified session.
     *
     * @param sessionId the session identifier
     * @param goId the go identifier
     * @return the GoEntity
     * @throws ResponseStatusException if the go is not found or doesn't belong to the session
     */
    private GoEntity findGoInSessionOrThrow(Long sessionId, Long goId) {
        GoEntity go = goRepository.findById(goId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Go not found"));

        if (go.getSession() == null || !go.getSession().getId().equals(sessionId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Go does not belong to session");
        }

        return go;
    }
}
