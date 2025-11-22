package de.othr.crusher.controller;

import de.othr.crusher.model.Gym;
import de.othr.crusher.repository.GymRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin/gyms")
public class GymController {

    private final GymRepository gymRepository;

    public GymController(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @GetMapping
    public String showAllGyms(Model model) {

        model.addAttribute("gyms", gymRepository.findAll());
        model.addAttribute("newGym", new Gym());

        return "pages/admin/gyms";
    }

    @GetMapping("/{id}")
    public String showGymForId(@PathVariable("id") long id, Model model) {

        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        model.addAttribute("gym", gym);

        return "pages/admin/gym";
    }

    @PostMapping
    public String createGym(@Valid @ModelAttribute("newGym") Gym newGym, BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("gyms", gymRepository.findAll());
            return "pages/admin/gyms";
        }

        gymRepository.save(newGym);
        return "redirect:/admin/gyms";
    }

    @DeleteMapping("/{id}")
    public String deleteGym(@PathVariable("id") long id) {
        gymRepository.deleteById(id);
        return "redirect:/admin/gyms";
    }
}
