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
     * Displays details for a specific sector based on gym and sector IDs.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @param model Spring model to pass data to the view
     * @return view name for the sector detail page
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
     *
     * @param gymId identifier of the parent gym
     * @param model Spring model to pass data to the view
     * @return view name for the sector creation form
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
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @param model Spring model to pass data to the view
     * @return view name for the sector edit form
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
     * Creates a new sector for the given gym. Validates input and either redisplays
     * the form with errors or saves the new sector.
     *
     * @param gymId identifier of the parent gym
     * @param sector sector payload from the form
     * @param result validation result
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the sector detail page or back to the form on validation errors
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
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @param formSector sector payload from the form
     * @param result validation result
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the detail page or back to edit when validation errors occur
     */
    @PutMapping("/{sectorId}")
    public String updateSector(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            @Valid @ModelAttribute("sector") SectorEntity formSector,
            BindingResult result,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        if (result.hasErrors()) {
            formSector.setId(sector.getId());
            formSector.setGym(gym);
            model.addAttribute("gym", gym);
            model.addAttribute("sector", formSector);
            return "pages/admin/sector-edit";
        }

        sector.setName(formSector.getName());
        sector.setDescription(formSector.getDescription());

        if (sector.getImagePath() == null || sector.getImagePath().isBlank()) {
            sector.setImagePath(DEFAULT_IMAGE_PATH);
        }

        sectorRepository.save(sector);
        return "redirect:/admin/gyms/" + gymId + "/sectors/" + sector.getId();
    }

    /**
     * Deletes an existing sector.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @return redirect to the gym detail page
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
