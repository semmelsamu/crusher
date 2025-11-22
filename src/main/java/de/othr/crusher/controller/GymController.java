package de.othr.crusher.controller;

import de.othr.crusher.model.Gym;
import de.othr.crusher.repository.GymRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


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

        return "pages/admin/gyms";
    }

    @GetMapping("/{id}")
    public String showGymForId(@PathVariable("id") long id, Model model) {

        Gym gym = gymRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gym not found"));

        model.addAttribute("gym", gym);

        return "pages/admin/gym";
    }
}
