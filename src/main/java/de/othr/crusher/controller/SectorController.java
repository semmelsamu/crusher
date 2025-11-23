package de.othr.crusher.controller;

import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.SectorEntity;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.SectorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
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

/**
 * Controller for managing sectors within a gym in the admin area.
 * <p>
 * Provides basic CRUD operations. Image upload/remove is deferred; a default image is used
 * until upload support is added.
 * </p>
 */
@Controller
@RequestMapping("/admin/gyms/{gymId}/sectors")
public class SectorController {

    private static final String DEFAULT_IMAGE_PATH = "/images/default-sector.svg";

    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;

    public SectorController(GymRepository gymRepository, SectorRepository sectorRepository) {
        this.gymRepository = gymRepository;
        this.sectorRepository = sectorRepository;
    }

    /**
     * Displays details for a specific sector.
     */
    @GetMapping("/{sectorId}")
    public String showSector(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        return "pages/admin/sector";
    }

    /**
     * Displays the form for creating a new sector for a gym.
     */
    @GetMapping("/new")
    public String showCreateForm(@PathVariable("gymId") long gymId, Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = new SectorEntity();
        sector.setGym(gym);
        sector.setImagePath(DEFAULT_IMAGE_PATH);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        return "pages/admin/sector-edit";
    }

    /**
     * Displays the form for editing an existing sector.
     */
    @GetMapping("/{sectorId}/edit")
    public String showEditForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        return "pages/admin/sector-edit";
    }

    /**
     * Creates a new sector for the given gym.
     */
    @PostMapping
    public String createSector(
            @PathVariable("gymId") long gymId,
            @Valid @ModelAttribute("sector") SectorEntity sector,
            BindingResult result,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);

        if (result.hasErrors()) {
            model.addAttribute("gym", gym);
            return "pages/admin/sector-edit";
        }

        sector.setGym(gym);
        if (sector.getImagePath() == null || sector.getImagePath().isBlank()) {
            sector.setImagePath(DEFAULT_IMAGE_PATH);
        }

        SectorEntity saved = sectorRepository.save(sector);
        return "redirect:/admin/gyms/" + gymId + "/sectors/" + saved.getId();
    }

    /**
     * Updates an existing sector.
     */
    @PutMapping("/{sectorId}")
    public String updateSector(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            @Valid @ModelAttribute("sector") SectorEntity formSector,
            BindingResult result,
            @RequestParam(value = "removeImage", required = false) boolean removeImage,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        if (result.hasErrors()) {
            model.addAttribute("gym", gym);
            return "pages/admin/sector-edit";
        }

        sector.setName(formSector.getName());
        sector.setDescription(formSector.getDescription());

        if (removeImage) {
            sector.setImagePath(DEFAULT_IMAGE_PATH);
        } else if (sector.getImagePath() == null || sector.getImagePath().isBlank()) {
            sector.setImagePath(DEFAULT_IMAGE_PATH);
        }

        sectorRepository.save(sector);
        if (removeImage) {
            return "redirect:/admin/gyms/" + gymId + "/sectors/" + sector.getId() + "/edit";
        }
        return "redirect:/admin/gyms/" + gymId + "/sectors/" + sector.getId();
    }

    /**
     * Deletes an existing sector.
     */
    @DeleteMapping("/{sectorId}")
    public String deleteSector(
            @PathVariable("gymId") long gymId, @PathVariable("sectorId") long sectorId) {
        findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);
        sectorRepository.delete(sector);
        return "redirect:/admin/gyms/" + gymId;
    }

    private GymEntity findGymOrThrow(long gymId) {
        return gymRepository
                .findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }

    private SectorEntity findSectorInGymOrThrow(long gymId, long sectorId) {
        SectorEntity sector = sectorRepository
                .findById(sectorId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector not found"));

        if (sector.getGym() == null || sector.getGym().getId() == null
                || !sector.getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector does not belong to gym");
        }
        return sector;
    }
}
