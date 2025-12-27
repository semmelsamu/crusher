package de.othr.crusher.controller;

import de.othr.crusher.model.*;
import de.othr.crusher.repository.BoulderRatingRepository;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.ProjectRepository;
import de.othr.crusher.repository.SectorRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Controller for managing the dashboard and boulder views.
 * Provides endpoints for viewing the dashboard and browsing boulders.
 */
@Controller
public class DashboardController {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BoulderRepository boulderRepository;
    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final GradeRepository gradeRepository;
    private final ProjectRepository projectRepository;
    private final GoRepository goRepository;
    private final BoulderRatingRepository ratingRepository;

    public DashboardController(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            BoulderRepository boulderRepository,
            GymRepository gymRepository,
            SectorRepository sectorRepository,
            GradeRepository gradeRepository,
            ProjectRepository projectRepository,
            GoRepository goRepository,
            BoulderRatingRepository ratingRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.boulderRepository = boulderRepository;
        this.gymRepository = gymRepository;
        this.sectorRepository = sectorRepository;
        this.gradeRepository = gradeRepository;
        this.projectRepository = projectRepository;
        this.goRepository = goRepository;
        this.ratingRepository = ratingRepository;
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

        return "pages/dashboard";
    }

    /**
     * Displays all boulders with optional filtering by gym, sector, and grades.
     *
     * @param gymId optional gym ID to filter by
     * @param sectorId optional sector ID to filter by
     * @param gradeIds optional list of grade IDs to filter by
     * @param projectOnly whether to show only boulders marked as projects by the current user
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the boulders page
     */
    @GetMapping("/boulders")
    @Transactional(readOnly = true)
    public String showAllBoulders(
            @RequestParam(value = "gymId", required = false) Long gymId,
            @RequestParam(value = "sectorId", required = false) Long sectorId,
            @RequestParam(value = "gradeIds", required = false) List<Long> gradeIds,
            @RequestParam(value = "projectOnly", required = false, defaultValue = "false") boolean projectOnly,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);

        // Fetch all gyms for the dropdown
        List<GymEntity> gyms = gymRepository.findAll();

        // Fetch sectors and grades for the selected gym
        List<SectorEntity> sectors = List.of();
        List<GradeEntity> grades = List.of();
        if (gymId != null) {
            sectors = sectorRepository.findByGymId(gymId);
            grades = gradeRepository.findByGymId(gymId);
        } else {
            sectors = sectorRepository.findAll();
            grades = gradeRepository.findAll();
        }

        // Filter boulders based on selected criteria
        List<BoulderEntity> boulders;
        if (sectorId != null) {
            // Filter by specific sector
            if (gradeIds != null && !gradeIds.isEmpty()) {
                boulders = boulderRepository.findBySectorIdAndGradeIdIn(sectorId, gradeIds);
            } else {
                boulders = boulderRepository.findBySectorId(sectorId);
            }
        } else if (gymId != null) {
            // Filter by gym
            if (gradeIds != null && !gradeIds.isEmpty()) {
                boulders = boulderRepository.findBySectorGymIdAndGradeIdIn(gymId, gradeIds);
            } else {
                boulders = boulderRepository.findBySectorGymId(gymId);
            }
        } else {
            // No filters - show all boulders
            boulders = boulderRepository.findAll();
        }

        // Load project marks for current user (once) to drive UI and optional filtering
        Set<Long> projectBoulderIds = projectRepository.findByUserId(user.getId()).stream()
                .map(project -> project.getBoulder() != null ? project.getBoulder().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (projectOnly) {
            boulders = boulders.stream()
                    .filter(boulder -> projectBoulderIds.contains(boulder.getId()))
                    .toList();
        }

        // Load ratings for current user
        Map<Long, Integer> boulderRatings = ratingRepository.findByUserId(user.getId()).stream()
                .collect(Collectors.toMap(
                        rating -> rating.getBoulder().getId(),
                        rating -> rating.getRating()
                ));

        // Add attributes to model
        model.addAttribute("boulders", boulders);
        model.addAttribute("gyms", gyms);
        model.addAttribute("sectors", sectors);
        model.addAttribute("grades", grades);
        model.addAttribute("selectedGymId", gymId);
        model.addAttribute("selectedSectorId", sectorId);
        model.addAttribute("selectedGradeIds", gradeIds != null ? gradeIds : List.of());
        model.addAttribute("projectBoulderIds", projectBoulderIds);
        model.addAttribute("projectOnly", projectOnly);
        model.addAttribute("boulderRatings", boulderRatings);

        return "pages/boulders";
    }

    /**
     * Displays details for a specific boulder.
     *
     * @param id boulder ID
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the boulder detail page
     */
    @GetMapping("/boulders/{id}")
    @Transactional(readOnly = true)
    public String showBoulder(@PathVariable("id") Long id, Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);
        BoulderEntity boulder = boulderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        // Load project status for the current user
        Set<Long> projectBoulderIds = projectRepository.findByUserId(user.getId()).stream()
                .map(project -> project.getBoulder() != null ? project.getBoulder().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // Get all goes for this boulder
        var allGoes = goRepository.findByBoulderIdOrderByTimestampDesc(boulder.getId());

        // Count total tries (all goes)
        long totalTries = allGoes.size();

        // Count total ascents (finished goes) for this boulder
        long ascentsCount = allGoes.stream()
                .filter(go -> go.getResult() != null && go.getResult() == GoResult.FINISHED)
                .count();

        // Get current user's rating for this boulder
        Integer currentRating = ratingRepository.findByUserIdAndBoulderId(user.getId(), boulder.getId())
                .map(rating -> rating.getRating())
                .orElse(0);

        // Calculate average rating for this boulder
        List<BoulderRatingEntity> allRatings = ratingRepository.findByBoulderId(boulder.getId());
        Double averageRating = allRatings.isEmpty() ? null :
                allRatings.stream()
                        .mapToInt(BoulderRatingEntity::getRating)
                        .average()
                        .orElse(0.0);

        model.addAttribute("boulder", boulder);
        model.addAttribute("isProject", projectBoulderIds.contains(boulder.getId()));
        model.addAttribute("ascentsCount", ascentsCount);
        model.addAttribute("totalTries", totalTries);
        model.addAttribute("currentRating", currentRating);
        model.addAttribute("averageRating", averageRating);

        return "pages/boulder-detail";
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
