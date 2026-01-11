package de.othr.crusher.controller;

import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.NoticeEntity;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.NoticeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Map;

/**
 * Controller for managing notices within a gym in the admin area.
 */
@Controller
@RequestMapping("/admin/gyms/{gymId}/notices")
public class NoticeController {

    private final NoticeRepository noticeRepository;
    private final GymRepository gymRepository;

    /**
     * Creates a new NoticeController with the given repositories.
     *
     * @param noticeRepository repository for accessing notice data
     * @param gymRepository repository for accessing gym data
     */
    public NoticeController(NoticeRepository noticeRepository, GymRepository gymRepository) {
        this.noticeRepository = noticeRepository;
        this.gymRepository = gymRepository;
    }

    /**
     * Displays the edit form for an existing notice.
     *
     * @param gymId identifier of the parent gym
     * @param noticeId identifier of the notice
     * @param model Spring model to pass data to the view
     * @return view name for the notice edit page
     */
    @GetMapping("/{noticeId}/update")
    public String showEditForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("noticeId") long noticeId,
            Model model) {
        NoticeEntity notice = findNoticeInGymOrThrow(gymId, noticeId);
        model.addAttribute("gym", notice.getGym());
        model.addAttribute("notice", notice);
        return "pages/admin/gyms/notices/update";
    }

    /**
     * Displays the form for creating a new notice.
     *
     * @param gymId identifier of the parent gym
     * @param model Spring model to pass data to the view
     * @return view name for the notice creation page
     */
    @GetMapping("/create")
    public String showCreateForm(@PathVariable("gymId") long gymId, Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        NoticeEntity notice = new NoticeEntity();
        notice.setGym(gym);

        model.addAttribute("gym", gym);
        model.addAttribute("notice", notice);
        return "pages/admin/gyms/notices/create";
    }

    /**
     * Handles the creation of a new notice. Validates input and either redisplays
     * the form with errors or saves the new notice.
     *
     * @param gymId identifier of the parent gym
     * @param formNotice notice object submitted from the form
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the gym detail page or the form view if errors occur
     */
    @PostMapping
    public String createNotice(
            @PathVariable("gymId") long gymId,
            @Valid @ModelAttribute("notice") NoticeEntity formNotice,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);

        if (result.hasErrors()) {
            model.addAttribute("gym", gym);
            model.addAttribute("notice", formNotice);
            return "pages/admin/gyms/notices/create";
        }

        formNotice.setGym(gym);
        noticeRepository.save(formNotice);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Notice created successfully!"
        ));

        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Updates an existing notice. Validates input and either redisplays
     * the form with errors or saves the changes.
     *
     * @param gymId identifier of the parent gym
     * @param noticeId identifier of the notice
     * @param formNotice notice object submitted from the form
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the gym detail page or the form view if errors occur
     */
    @PutMapping("/{noticeId}")
    public String updateNotice(
            @PathVariable("gymId") long gymId,
            @PathVariable("noticeId") long noticeId,
            @Valid @ModelAttribute("notice") NoticeEntity formNotice,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        NoticeEntity notice = findNoticeInGymOrThrow(gymId, noticeId);

        if (result.hasErrors()) {
            formNotice.setId(notice.getId());
            formNotice.setGym(notice.getGym());
            model.addAttribute("gym", notice.getGym());
            model.addAttribute("notice", formNotice);
            return "pages/admin/gyms/notices/update";
        }

        notice.setTitle(formNotice.getTitle());
        notice.setMessage(formNotice.getMessage());
        noticeRepository.save(notice);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Notice updated successfully!"
        ));

        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Deletes a notice by its ID.
     *
     * @param gymId identifier of the parent gym
     * @param noticeId identifier of the notice
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the gym detail page
     */
    @DeleteMapping("/{noticeId}")
    public String deleteNotice(
            @PathVariable("gymId") long gymId,
            @PathVariable("noticeId") long noticeId,
            RedirectAttributes redirectAttributes) {
        NoticeEntity notice = findNoticeInGymOrThrow(gymId, noticeId);
        noticeRepository.delete(notice);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Notice deleted successfully!"
        ));

        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Finds a notice by ID and validates it belongs to the specified gym.
     *
     * @param gymId identifier of the parent gym
     * @param noticeId identifier of the notice
     * @return the notice entity
     * @throws ResponseStatusException if notice not found or doesn't belong to gym
     */
    private NoticeEntity findNoticeInGymOrThrow(long gymId, long noticeId) {
        NoticeEntity notice = noticeRepository
                .findById(noticeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));

        if (notice.getGym() == null || notice.getGym().getId() == null
                || !notice.getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice does not belong to gym");
        }
        return notice;
    }

    /**
     * Finds a gym by ID or throws a ResponseStatusException if not found.
     *
     * @param gymId identifier of the gym
     * @return the gym entity
     * @throws ResponseStatusException if gym not found
     */
    private GymEntity findGymOrThrow(long gymId) {
        return gymRepository
                .findById(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }
}
