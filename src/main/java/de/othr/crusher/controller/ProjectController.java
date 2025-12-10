package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.ProjectEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.ProjectRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controller for toggling project marks on boulders for the current user.
 */
@Controller
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final BoulderRepository boulderRepository;
    private final UserRepository userRepository;

    public ProjectController(ProjectRepository projectRepository, BoulderRepository boulderRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.boulderRepository = boulderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Toggles the project state for the current user on a given boulder.
     * Adds the project when missing, removes it when present.
     *
     * @param boulderId identifier of the boulder
     * @param gymId optional gym filter to preserve on redirect
     * @param sectorId optional sector filter to preserve on redirect
     * @param gradeIds optional grade filters to preserve on redirect
     * @param projectOnly whether the projects-only filter was active
     * @param principal authenticated user
     * @param redirectAttributes attributes to preserve filters and toast
     * @return redirect to the boulders overview
     */
    @PostMapping("/boulders/{boulderId}/project/toggle")
    @Transactional
    public String toggleProject(
            @PathVariable("boulderId") Long boulderId,
            @RequestParam(value = "gymId", required = false) Long gymId,
            @RequestParam(value = "sectorId", required = false) Long sectorId,
            @RequestParam(value = "gradeIds", required = false) List<Long> gradeIds,
            @RequestParam(value = "projectOnly", required = false, defaultValue = "false") boolean projectOnly,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        BoulderEntity boulder = boulderRepository.findById(boulderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        projectRepository.findByUserIdAndBoulderId(user.getId(), boulderId)
                .ifPresentOrElse(
                        projectRepository::delete,
                        () -> {
                            ProjectEntity project = new ProjectEntity();
                            project.setUser(user);
                            project.setBoulder(boulder);
                            projectRepository.save(project);
                        }
                );

        boolean nowActive = projectRepository.findByUserIdAndBoulderId(user.getId(), boulderId).isPresent();
        redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "success",
                "message", nowActive ? "Boulder added to your projects!" : "Boulder removed from your projects."
        ));
        preserveFilters(redirectAttributes, gymId, sectorId, gradeIds, projectOnly);
        return "redirect:/boulders";
    }

    /**
     * Marks a boulder as a project for the current user. Idempotent: adding an existing
     * project is treated as success without error.
     *
     * @param boulderId identifier of the boulder
     * @param gymId optional gym filter to preserve on redirect
     * @param sectorId optional sector filter to preserve on redirect
     * @param gradeIds optional grade filters to preserve on redirect
     * @param projectOnly whether the projects-only filter was active
     * @param principal authenticated user
     * @param redirectAttributes attributes to preserve filters and toast
     * @return redirect to the boulders overview
     */
    @PostMapping("/boulders/{boulderId}/project")
    @Transactional
    public String addProject(
            @PathVariable("boulderId") Long boulderId,
            @RequestParam(value = "gymId", required = false) Long gymId,
            @RequestParam(value = "sectorId", required = false) Long sectorId,
            @RequestParam(value = "gradeIds", required = false) List<Long> gradeIds,
            @RequestParam(value = "projectOnly", required = false, defaultValue = "false") boolean projectOnly,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        BoulderEntity boulder = boulderRepository.findById(boulderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        projectRepository.findByUserIdAndBoulderId(user.getId(), boulderId).ifPresentOrElse(
                existing -> { /* Already added, nothing to do */ },
                () -> {
                    ProjectEntity project = new ProjectEntity();
                    project.setUser(user);
                    project.setBoulder(boulder);
                    projectRepository.save(project);
                }
        );

        redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "success",
                "message", "Boulder added to your projects!"
        ));
        preserveFilters(redirectAttributes, gymId, sectorId, gradeIds, projectOnly);
        return "redirect:/boulders";
    }

    /**
     * Removes a boulder from the current user's projects. Idempotent: removing a missing
     * project is treated as success without error.
     *
     * @param boulderId identifier of the boulder
     * @param gymId optional gym filter to preserve on redirect
     * @param sectorId optional sector filter to preserve on redirect
     * @param gradeIds optional grade filters to preserve on redirect
     * @param projectOnly whether the projects-only filter was active
     * @param principal authenticated user
     * @param redirectAttributes attributes to preserve filters and toast
     * @return redirect to the boulders overview
     */
    @DeleteMapping("/boulders/{boulderId}/project")
    @Transactional
    public String removeProject(
            @PathVariable("boulderId") Long boulderId,
            @RequestParam(value = "gymId", required = false) Long gymId,
            @RequestParam(value = "sectorId", required = false) Long sectorId,
            @RequestParam(value = "gradeIds", required = false) List<Long> gradeIds,
            @RequestParam(value = "projectOnly", required = false, defaultValue = "false") boolean projectOnly,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        projectRepository.findByUserIdAndBoulderId(user.getId(), boulderId)
                .ifPresent(projectRepository::delete);

        redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "success",
                "message", "Boulder removed from your projects."
        ));
        preserveFilters(redirectAttributes, gymId, sectorId, gradeIds, projectOnly);
        return "redirect:/boulders";
    }

    private UserEntity findUserByPrincipal(Principal principal) {
        return userRepository.findByName(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private void preserveFilters(RedirectAttributes redirectAttributes, Long gymId, Long sectorId, List<Long> gradeIds, boolean projectOnly) {
        if (gymId != null) {
            redirectAttributes.addAttribute("gymId", gymId);
        }
        if (sectorId != null) {
            redirectAttributes.addAttribute("sectorId", sectorId);
        }
        if (gradeIds != null && !gradeIds.isEmpty()) {
            redirectAttributes.addAttribute("gradeIds", gradeIds);
        }
        if (projectOnly) {
            redirectAttributes.addAttribute("projectOnly", true);
        }
    }
}
