package de.othr.crusher.controller;

import de.othr.crusher.model.Gym;
import de.othr.crusher.repository.GymRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


/**
 * Controller for managing gyms in the admin area.
 * Provides endpoints for listing, viewing, creating and deleting gyms.
 */
@Controller
@RequestMapping("/admin/gyms")
public class GymController {

    private final GymRepository gymRepository;

    /**
     * Creates a new GymController with the given repository.
     *
     * @param gymRepository repository for accessing gym data
     */
    public GymController(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    /**
     * Displays a list of all gyms and a form for creating a new gym.
     *
     * @param model Spring model to pass data to the view
     * @return view name for the gyms overview page
     */
    @GetMapping
    public String showAllGyms(Model model) {
        model.addAttribute("gyms", gymRepository.findAll());
        model.addAttribute("newGym", new Gym());
        return "pages/admin/gyms";
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
        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        model.addAttribute("gym", gym);
        return "pages/admin/gym";
    }

    /**
     * Handles the creation of a new gym. Validates input and either redisplays
     * the form with errors or saves the new gym.
     *
     * @param newGym gym object submitted from the form
     * @param result validation result
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the gym list or the form view if errors occur
     */
    @PostMapping
    public String createGym(@Valid @ModelAttribute("newGym") Gym newGym, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("gyms", gymRepository.findAll());
            return "pages/admin/gyms";
        }

        gymRepository.save(newGym);
        return "redirect:/admin/gyms";
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
}

