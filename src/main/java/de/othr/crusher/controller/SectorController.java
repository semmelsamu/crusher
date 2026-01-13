package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.SectorEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.SectorRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.service.EmailService;
import de.othr.crusher.service.SectorImageStorageService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
    private final SessionRepository sessionRepository;
    private final EmailService emailService;

    public SectorController(
            GymRepository gymRepository,
            SectorRepository sectorRepository,
            BoulderRepository boulderRepository,
            SectorImageStorageService sectorImageStorageService,
            SessionRepository sessionRepository,
            EmailService emailService) {
        this.gymRepository = gymRepository;
        this.sectorRepository = sectorRepository;
        this.boulderRepository = boulderRepository;
        this.sectorImageStorageService = sectorImageStorageService;
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
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
        List<BoulderEntity> boulders = boulderRepository.findBySectorIdAndDeletedFalse(sectorId);

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
     * Soft-deletes an existing sector by setting its deleted flag.
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
        sector.setDeleted(true);
        sectorRepository.save(sector);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "message", "Sector deleted successfully!"
        ));

        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Publishes unpublished boulders in this sector by sending email notifications
     * to all users who have ever had a session at this gym, and marks them as published.
     *
     * @param gymId identifier of the parent gym
     * @param sectorId identifier of the sector
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the sector detail page
     */
    @PostMapping("/{sectorId}/publish-new-boulders")
    public String publishNewBoulders(
            @PathVariable("gymId") long gymId,
            @PathVariable("sectorId") long sectorId,
            RedirectAttributes redirectAttributes) {
        GymEntity gym = findGymOrThrow(gymId);
        SectorEntity sector = findSectorInGymOrThrow(gymId, sectorId);

        // Find all unpublished boulders in this sector
        List<BoulderEntity> unpublishedBoulders = boulderRepository.findBySectorIdAndPublishedFalse(sectorId);

        if (unpublishedBoulders.isEmpty()) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "No unpublished boulders",
                "message", "All boulders in this sector have already been published."
            ));
            return "redirect:/admin/gyms/" + gymId + "/sectors/" + sectorId;
        }

        // Find all users who have ever had a session at this gym
        List<UserEntity> users = sessionRepository.findDistinctUsersByGymId(gymId);

        if (users.isEmpty()) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "No users to notify",
                "message", "No users have had sessions at this gym yet."
            ));
            return "redirect:/admin/gyms/" + gymId + "/sectors/" + sectorId;
        }

        // Send email to each user
        int emailsSent = 0;
        for (UserEntity user : users) {
            try {
                emailService.sendNewBouldersEmail(user.getEmail(), user.getName(), gym.getName(), sector.getName(), unpublishedBoulders);
                emailsSent++;
            } catch (Exception e) {
                System.err.println("Failed to send email to " + user.getEmail() + ": " + e.getMessage());
            }
        }

        // Mark all boulders as published
        for (BoulderEntity boulder : unpublishedBoulders) {
            boulder.setPublished(true);
        }
        boulderRepository.saveAll(unpublishedBoulders);

        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "title", "Notifications sent",
            "message", String.format("Sent %d email(s) about %d new boulder(s).", emailsSent, unpublishedBoulders.size())
        ));

        return "redirect:/admin/gyms/" + gymId + "/sectors/" + sectorId;
    }

    private GymEntity findGymOrThrow(long gymId) {
        return gymRepository
                .findByIdAndDeletedFalse(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }

    private SectorEntity findSectorInGymOrThrow(long gymId, long sectorId) {
        SectorEntity sector = sectorRepository
                .findByIdAndDeletedFalse(sectorId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector not found"));

        if (sector.getGym() == null || sector.getGym().getId() == null
                || !sector.getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector does not belong to gym");
        }
        return sector;
    }
}
