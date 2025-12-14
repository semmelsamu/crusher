package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.GoEntity;
import de.othr.crusher.model.GoResult;
import de.othr.crusher.model.SectorEntity;
import de.othr.crusher.model.SessionEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.SectorRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing climbing attempts (goes) within a session.
 * <p>
 * Provides CRUD operations for tracking boulder attempts during a climbing session.
 * All operations are nested under the session resource path.
 * </p>
 */
@Controller
@RequestMapping("/sessions/{sessionId}/goes")
public class GoController {

    private final GoRepository goRepository;
    private final SessionRepository sessionRepository;
    private final BoulderRepository boulderRepository;
    private final SectorRepository sectorRepository;
    private final UserRepository userRepository;

    public GoController(
            GoRepository goRepository,
            SessionRepository sessionRepository,
            BoulderRepository boulderRepository,
            SectorRepository sectorRepository,
            UserRepository userRepository) {
        this.goRepository = goRepository;
        this.sessionRepository = sessionRepository;
        this.boulderRepository = boulderRepository;
        this.sectorRepository = sectorRepository;
        this.userRepository = userRepository;
    }

    /**
     * Displays the boulder selection step for creating a new go in a session.
     *
     * @param sessionId identifier of the parent session
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the boulder selection step
     */
    @GetMapping("/create")
    @Transactional(readOnly = true)
    public String showBoulderSelection(
            @PathVariable("sessionId") Long sessionId,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);

        List<SectorEntity> sectors = sectorRepository.findByGymId(session.getGym().getId());

        // Load and sort boulders for each sector by grade (vScale)
        Map<Long, List<BoulderEntity>> sectorBoulders = new HashMap<>();
        for (SectorEntity sector : sectors) {
            List<BoulderEntity> boulders = boulderRepository.findBySectorId(sector.getId());
            boulders.sort((b1, b2) -> {
                String v1 = b1.getGrade().getVScale();
                String v2 = b2.getGrade().getVScale();
                // Extract numeric value from V-scale (e.g., "V0" -> 0, "V10" -> 10)
                int grade1 = Integer.parseInt(v1.substring(1));
                int grade2 = Integer.parseInt(v2.substring(1));
                return Integer.compare(grade1, grade2);
            });
            sectorBoulders.put(sector.getId(), boulders);
        }

        model.addAttribute("currentSession", session);
        model.addAttribute("sectors", sectors);
        model.addAttribute("sectorBoulders", sectorBoulders);
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard"),
                Map.of("label", "Session", "url", "/sessions/" + sessionId),
                Map.of("label", "Record Go", "url", "")
        ));

        return "pages/goes/select-boulder";
    }

    /**
     * Displays the result selection step for creating a new go with a selected boulder.
     *
     * @param sessionId identifier of the parent session
     * @param boulderId identifier of the selected boulder
     * @param createAnother whether to remain on the create flow after saving
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the go creation form
     */
    @GetMapping("/create/{boulderId}")
    @Transactional(readOnly = true)
    public String showCreateForm(
            @PathVariable("sessionId") Long sessionId,
            @PathVariable("boulderId") Long boulderId,
            @RequestParam(required = false, defaultValue = "false") boolean createAnother,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);
        BoulderEntity boulder = boulderRepository.findById(boulderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        GoEntity go = new GoEntity();
        go.setSession(session);
        go.setBoulder(boulder);

        model.addAttribute("currentSession", session);
        model.addAttribute("go", go);
        model.addAttribute("boulder", boulder);
        model.addAttribute("availableResults", GoResult.values());
        model.addAttribute("createAnother", createAnother);
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard"),
                Map.of("label", "Session", "url", "/sessions/" + sessionId),
                Map.of("label", "Record Go", "url", "/sessions/" + sessionId + "/goes/create"),
                Map.of("label", "Select Result", "url", "")
        ));

        return "pages/goes/create-result";
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

        model.addAttribute("currentSession", session);
        model.addAttribute("go", go);
        model.addAttribute("boulder", go.getBoulder());
        model.addAttribute("availableResults", GoResult.values());
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Dashboard", "url", "/dashboard"),
                Map.of("label", "Session", "url", "/sessions/" + sessionId),
                Map.of("label", "Go #" + goId, "url", "/sessions/" + sessionId + "/goes/" + goId),
                Map.of("label", "Edit", "url", "")
        ));

        return "pages/goes/edit";
    }

    /**
     * Creates a new go for the given session.
     *
     * @param sessionId identifier of the parent session
     * @param go go payload from the form
     * @param createAnother whether to create another go for the same boulder
     * @param result validation result
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the session detail page, back to create form, or back on validation errors
     */
    @PostMapping
    @Transactional
    public String createGo(
            @PathVariable("sessionId") Long sessionId,
            @ModelAttribute("go") GoEntity go,
            BindingResult result,
            @RequestParam(required = false, defaultValue = "false") boolean createAnother,
            @RequestParam(value = "trackProgress", required = false, defaultValue = "false") boolean trackProgress,
            Principal principal,
            RedirectAttributes redirectAttributes,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);

        BoulderEntity boulder = null;
        if (go.getBoulder() != null && go.getBoulder().getId() != null) {
            boulder = boulderRepository.findById(go.getBoulder().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid boulder"));
            go.setBoulder(boulder);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Boulder is required");
        }

        go.setSession(session);
        validateProgressedHold(go, boulder, trackProgress, result);

        if (result.hasErrors()) {
            model.addAttribute("currentSession", session);
            model.addAttribute("go", go);
            model.addAttribute("boulder", boulder);
            model.addAttribute("availableResults", GoResult.values());
            model.addAttribute("createAnother", createAnother);
            model.addAttribute("breadcrumb", List.of(
                    Map.of("label", "Home", "url", "/"),
                    Map.of("label", "Dashboard", "url", "/dashboard"),
                    Map.of("label", "Session", "url", "/sessions/" + sessionId),
                    Map.of("label", "Record Go", "url", "/sessions/" + sessionId + "/goes/create"),
                    Map.of("label", "Select Result", "url", "")
            ));
            return "pages/goes/create-result";
        }

        // Set timestamp when save button is clicked
        go.setTimestamp(LocalDateTime.now());
        goRepository.save(go);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Go created successfully!"
        ));

        // If "create another" is checked, redirect back to create form with same boulder
        if (createAnother && go.getBoulder() != null) {
            return "redirect:/sessions/" + sessionId + "/goes/create/" + go.getBoulder().getId() + "?createAnother=true";
        }

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
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the session detail page or back to edit when validation errors occur
     */
    @PutMapping("/{goId}")
    @Transactional
    public String updateGo(
            @PathVariable("sessionId") Long sessionId,
            @PathVariable("goId") Long goId,
            @ModelAttribute("go") GoEntity formGo,
            BindingResult result,
            @RequestParam(value = "trackProgress", required = false, defaultValue = "false") boolean trackProgress,
            Principal principal,
            RedirectAttributes redirectAttributes,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        SessionEntity session = findSessionAndVerifyOwnership(sessionId, user);
        GoEntity go = findGoInSessionOrThrow(sessionId, goId);

        BoulderEntity boulder = null;
        if (formGo.getBoulder() != null && formGo.getBoulder().getId() != null) {
            boulder = boulderRepository.findById(formGo.getBoulder().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid boulder"));
        } else if (go.getBoulder() != null) {
            boulder = go.getBoulder();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Boulder is required");
        }

        formGo.setBoulder(boulder);
        formGo.setSession(session);
        validateProgressedHold(formGo, boulder, trackProgress, result);

        if (result.hasErrors()) {
            formGo.setId(go.getId());
            model.addAttribute("currentSession", session);
            model.addAttribute("go", formGo);
            model.addAttribute("boulder", boulder);
            model.addAttribute("availableResults", GoResult.values());
            model.addAttribute("breadcrumb", List.of(
                    Map.of("label", "Home", "url", "/"),
                    Map.of("label", "Dashboard", "url", "/dashboard"),
                    Map.of("label", "Session", "url", "/sessions/" + sessionId),
                    Map.of("label", "Go #" + goId, "url", "/sessions/" + sessionId + "/goes/" + goId),
                    Map.of("label", "Edit", "url", "")
            ));
            return "pages/goes/edit";
        }

        go.setBoulder(boulder);

        go.setResult(formGo.getResult());
        go.setProgressedHold(formGo.getProgressedHold());
        // Timestamp is intentionally not updated - preserve original timestamp
        goRepository.save(go);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Go updated successfully!"
        ));

        return "redirect:/sessions/" + sessionId;
    }

    /**
     * Deletes an existing go.
     *
     * @param sessionId identifier of the parent session
     * @param goId identifier of the go
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the session detail page
     */
    @DeleteMapping("/{goId}")
    @Transactional
    public String deleteGo(
            @PathVariable("sessionId") Long sessionId,
            @PathVariable("goId") Long goId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        findSessionAndVerifyOwnership(sessionId, user);
        GoEntity go = findGoInSessionOrThrow(sessionId, goId);

        goRepository.delete(go);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Go deleted successfully!"
        ));

        return "redirect:/sessions/" + sessionId;
    }

    private void validateProgressedHold(
            GoEntity go, BoulderEntity boulder, boolean trackProgress, BindingResult result) {
        if (!trackProgress) {
            go.setProgressedHold(null);
            return;
        }

        Integer progressedHold = go.getProgressedHold();
        if (progressedHold == null) {
            result.rejectValue("progressedHold", "progress.required", "Please select the last hold you reached");
            return;
        }

        if (progressedHold < 0) {
            result.rejectValue("progressedHold", "progress.invalid", "Progressed hold cannot be negative");
        }

        Integer holdsCount = boulder.getHoldsCount();
        if (holdsCount != null && progressedHold > holdsCount) {
            result.rejectValue(
                    "progressedHold",
                    "progress.tooHigh",
                    "Progressed hold cannot exceed the boulder's total holds");
        }
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
