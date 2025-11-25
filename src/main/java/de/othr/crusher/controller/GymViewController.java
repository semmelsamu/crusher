package de.othr.crusher.controller;

import de.othr.crusher.model.GymEntity;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.service.GradeService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for public gym views.
 * <p>
 * Provides endpoints to list gyms and to view a single gym with its sectors and grades.
 * </p>
 */
@Controller
@RequestMapping("/gyms")
public class GymViewController {

    private final GymRepository gymRepository;
    private final GradeService gradeService;

    public GymViewController(GymRepository gymRepository, GradeService gradeService) {
        this.gymRepository = gymRepository;
        this.gradeService = gradeService;
    }

    /**
     * Displays a list of all gyms.
     *
     * @param model Spring model to pass data to the view
     * @return view name for the gyms overview page
     */
    @GetMapping
    public String listGyms(Model model) {
        model.addAttribute("gyms", gymRepository.findAll());
        return "pages/gyms";
    }

    /**
     * Displays details for a specific gym.
     *
     * @param gymId identifier of the gym
     * @param model Spring model to pass data to the view
     * @return view name for the gym detail page
     */
    @GetMapping("/{gymId}")
    public String showGym(@PathVariable("gymId") long gymId, Model model) {
        GymEntity gym = gymRepository
                .findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

        model.addAttribute("gym", gym);
        model.addAttribute("grades", gradeService.findAllForGym(gymId));
        return "pages/gym";
    }
}
