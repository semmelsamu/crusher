package de.othr.crusher.controller;

import de.othr.crusher.model.GymEntity;
import de.othr.crusher.repository.GradeRepository;
import de.othr.crusher.repository.GymRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Map;


/**
 * Controller for managing gyms in the admin area.
 * Provides endpoints for listing, viewing, creating, editing and deleting gyms.
 */
@Controller
@RequestMapping("/admin/gyms")
public class GymController {

    private final GymRepository gymRepository;
    private final GradeRepository gradeRepository;

    /**
     * Creates a new GymController with the given repository.
     *
     * @param gymRepository repository for accessing gym data
     * @param gradeRepository repository for accessing grade data
     */
    public GymController(GymRepository gymRepository, GradeRepository gradeRepository) {
        this.gymRepository = gymRepository;
        this.gradeRepository = gradeRepository;
    }

    /**
     * Displays a list of all gyms.
     *
     * @param model Spring model to pass data to the view
     * @return view name for the gyms overview page
     */
    @GetMapping
    public String showAllGyms(Model model) {
        model.addAttribute("gyms", gymRepository.findAll());
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Admin Panel", "url", "/admin/gyms")
        ));
        return "pages/admin/gyms/all";
    }

    /**
     * Displays details for a specific gym based on the given ID.
     *
     * @param id gym ID
     * @param model Spring model to pass data to the view
     * @return view name for the gym detail page
     */
    @GetMapping("/{id}")
    public String showGymForId(@PathVariable("id") long id, Model model) {
        GymEntity gym = gymRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

        model.addAttribute("gym", gym);
        model.addAttribute("grades", gradeRepository.findByGymId(id));

        // Add breadcrumb navigation
        model.addAttribute("breadcrumb", List.of(
                Map.of("label", "Home", "url", "/"),
                Map.of("label", "Admin Panel", "url", "/admin/gyms"),
                Map.of("label", gym.getName(), "url", "/admin/gyms/" + gym.getId())
        ));

        return "pages/admin/gyms/detail";
    }

    /**
     * Displays the form for creating a new gym.
     *
     * @param model Spring model to pass data to the view
     * @return view name for the gym creation page
     */
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("gym", new GymEntity());
        return "pages/admin/gyms/create";
    }

    /**
     * Displays the edit form for an existing gym.
     *
     * @param id gym ID
     * @param model Spring model to pass data to the view
     * @return view name for the gym edit page
     */
    @GetMapping("/{id}/update")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        GymEntity gym = findGymOrThrow(id);
        model.addAttribute("gym", gym);
        return "pages/admin/gyms/update";
    }

    /**
     * Handles the creation of a new gym. Validates input and either redisplays
     * the form with errors or saves the new gym.
     *
     * @param gym gym object submitted from the form
     * @param result validation result
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the gym list or the form view if errors occur
     */
    @PostMapping
    public String createGym(@Valid @ModelAttribute("gym") GymEntity gym, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "pages/admin/gyms/create";
        }

        gymRepository.save(gym);
        return "redirect:/admin/gyms";
    }

    /**
     * Updates an existing gym. Validates input and either redisplays
     * the form with errors or saves the changes.
     *
     * @param id gym ID
     * @param formGym gym object submitted from the form
     * @param result validation result
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the gym detail page or the form view if errors occur
     */
    @PutMapping("/{id}")
    public String updateGym(
            @PathVariable("id") long id,
            @Valid @ModelAttribute("gym") GymEntity formGym,
            BindingResult result,
            Model model) {
        GymEntity gym = findGymOrThrow(id);

        if (result.hasErrors()) {
            formGym.setId(gym.getId());
            model.addAttribute("gym", formGym);
            return "pages/admin/gyms/update";
        }

        gym.setName(formGym.getName());
        gym.setStreet(formGym.getStreet());
        gym.setCity(formGym.getCity());
        gym.setEmail(formGym.getEmail());
        gymRepository.save(gym);
        return "redirect:/admin/gyms/" + id;
    }

    /**
     * Deletes a gym by its ID.
     *
     * @param id gym ID
     * @return redirect to the gym list
     */
    @DeleteMapping("/{id}")
    public String deleteGym(@PathVariable("id") long id) {
        gymRepository.deleteById(id);
        return "redirect:/admin/gyms";
    }


    private GymEntity findGymOrThrow(long gymId) {
        return gymRepository
                .findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }
}
