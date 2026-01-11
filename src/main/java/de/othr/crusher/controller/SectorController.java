package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.SectorEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.SectorRepository;
import de.othr.crusher.service.SectorImageStorageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing sectors within a gym in the admin area.
 * <p>
 * Provides CRUD operations with optional image upload and removal. A default image is used
 * when no custom image has been uploaded.
 * </p>
 */
@Controller
@RequestMapping("/admin/gyms/{gymId}/sectors")
public class SectorController {

    private static final String DEFAULT_IMAGE_PATH = "/images/default-sector.svg";

    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final BoulderRepository boulderRepository;
    private final SectorImageStorageService sectorImageStorageService;

    public SectorController(
            GymRepository gymRepository,
            SectorRepository sectorRepository,
            BoulderRepository boulderRepository,
            SectorImageStorageService sectorImageStorageService) {
        this.gymRepository = gymRepository;
        this.sectorRepository = sectorRepository;
        this.boulderRepository = boulderRepository;
        this.sectorImageStorageService = sectorImageStorageService;
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
        List<BoulderEntity> boulders = boulderRepository.findBySectorId(sectorId);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        model.addAttribute("boulders", boulders);
        
        return "pages/admin/sectors/detail";
    }

    /**
     * Displays the form for creating a new sector for a gym.
     *
     * @param gymId identifier of the parent gym
     * @param model Spring model to pass data to the view
     * @return view name for the sector creation form
     */
    @GetMapping("/create")
    public String showCreateForm(@PathVariable("gymId") long gymId, Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = new SectorEntity();
        sector.setGym(gym);
        sector.setImagePath(DEFAULT_IMAGE_PATH);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        return "pages/admin/sectors/create";
    }

    /**
     * Displays the form for editing an existing sector.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @param model Spring model to pass data to the view
     * @return view name for the sector edit form
     */
    @GetMapping("/{sectorId}/update")
    public String showEditForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        model.addAttribute("gym", gym);
        model.addAttribute("sector", sector);
        return "pages/admin/sectors/update";
    }

    /**
     * Creates a new sector for the given gym. Validates input and either redisplays
     * the form with errors or saves the new sector.
     *
     * @param gymId identifier of the parent gym
     * @param sector sector payload from the form
     * @param imageFile optional uploaded image for the sector
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the sector detail page or back to the form on validation errors
     */
    @PostMapping
    public String createSector(
            @PathVariable("gymId") long gymId,
            @Valid @ModelAttribute("sector") SectorEntity sector,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);

        if (result.hasErrors()) {
            if (sector.getImagePath() == null || sector.getImagePath().isBlank()) {
                sector.setImagePath(DEFAULT_IMAGE_PATH);
            }
            model.addAttribute("gym", gym);
            return "pages/admin/sectors/create";
        }

        sector.setGym(gym);
        if (sector.getImagePath() == null || sector.getImagePath().isBlank()) {
            sector.setImagePath(DEFAULT_IMAGE_PATH);
        }

        SectorEntity saved = sectorRepository.save(sector);

        String toastType = "success";
        String toastMessage = "Sector created successfully!";

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String storedPath = sectorImageStorageService.store(saved.getId(), imageFile);
                saved.setImagePath(storedPath);
                saved = sectorRepository.save(saved);
            } catch (IllegalArgumentException | IllegalStateException e) {
                toastType = "error";
                toastMessage = "Sector created, but image upload failed: " + e.getMessage();
            }
        }

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", toastType,
            "message", toastMessage
        ));

        return "redirect:/admin/gyms/" + gymId + "/sectors/" + saved.getId();
    }

    /**
     * Updates an existing sector.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @param formSector sector payload from the form
     * @param imageFile optional uploaded image for the sector
     * @param removeImage flag to reset the sector image to the default
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the detail page or back to edit when validation errors occur
     */
    @PutMapping("/{sectorId}")
    public String updateSector(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            @Valid @ModelAttribute("sector") SectorEntity formSector,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "removeImage", defaultValue = "false") boolean removeImage,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        if (result.hasErrors()) {
            formSector.setId(sector.getId());
            formSector.setGym(gym);
            if (formSector.getImagePath() == null || formSector.getImagePath().isBlank()) {
                formSector.setImagePath(
                        sector.getImagePath() != null ? sector.getImagePath() : DEFAULT_IMAGE_PATH);
            }
            model.addAttribute("gym", gym);
            model.addAttribute("sector", formSector);
            return "pages/admin/sectors/update";
        }

        sector.setName(formSector.getName());
        sector.setDescription(formSector.getDescription());

        String toastType = "success";
        String toastMessage = "Sector updated successfully!";

        if (removeImage) {
            sectorImageStorageService.deleteIfStored(sector.getImagePath());
            sector.setImagePath(DEFAULT_IMAGE_PATH);
        } else if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String storedPath = sectorImageStorageService.store(sector.getId(), imageFile);
                sectorImageStorageService.deleteIfStored(sector.getImagePath());
                sector.setImagePath(storedPath);
            } catch (IllegalArgumentException | IllegalStateException e) {
                toastType = "error";
                toastMessage = "Sector updated, but image upload failed: " + e.getMessage();
            }
        } else if (sector.getImagePath() == null || sector.getImagePath().isBlank()) {
            sector.setImagePath(DEFAULT_IMAGE_PATH);
        }

        sectorRepository.save(sector);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", toastType,
            "message", toastMessage
        ));

        return "redirect:/admin/gyms/" + gymId + "/sectors/" + sector.getId();
    }

    /**
     * Deletes an existing sector.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the gym detail page
     */
    @DeleteMapping("/{sectorId}")
    public String deleteSector(
            @PathVariable("gymId") long gymId, @PathVariable("sectorId") long sectorId, RedirectAttributes redirectAttributes) {
        findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);
        
        // Check if any boulders reference this sector
        List<BoulderEntity> referencingBoulders = boulderRepository.findBySectorId(sectorId);
        if (!referencingBoulders.isEmpty()) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error", 
                "message", "Cannot delete sector. Please delete all boulders in this sector first."
            ));
            return "redirect:/admin/gyms/" + gymId;
        }
        
        try {
            // Delete the associated image file first
            sectorImageStorageService.deleteIfStored(sector.getImagePath());
            sectorRepository.delete(sector);
            
            // Add success message for toast notification
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "success", 
                "message", "Sector deleted successfully!"
            ));
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error", 
                "message", "Cannot delete sector due to existing references."
            ));
        }

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
