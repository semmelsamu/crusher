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
 * Controller for managing notices.
 * Provides endpoints for listing, viewing, creating, editing and deleting notices.
 */
@Controller
@RequestMapping("/admin/notices")
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
     * Displays a list of all notices.
     *
     * @param model Spring model to pass data to the view
     * @return view name for the notices overview page
     */
    @GetMapping
    public String showAllNotices(Model model) {
        model.addAttribute("notices", noticeRepository.findAll());
        return "pages/admin/notices/all";
    }

    /**
     * Displays details for a specific notice based on the given ID.
     *
     * @param id notice ID
     * @param model Spring model to pass data to the view
     * @return view name for the notice detail page
     */
    @GetMapping("/{id}")
    public String showNoticeForId(@PathVariable("id") long id, Model model) {
        NoticeEntity notice = noticeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));

        model.addAttribute("notice", notice);
        return "pages/admin/notices/detail";
    }

    /**
     * Displays the form for creating a new notice.
     *
     * @param gymId optional gym ID to pre-select
     * @param model Spring model to pass data to the view
     * @return view name for the notice creation page
     */
    @GetMapping("/create")
    public String showCreateForm(@RequestParam(value = "gymId", required = false) Long gymId, Model model) {
        NoticeEntity notice = new NoticeEntity();
        if (gymId != null) {
            GymEntity gym = gymRepository.findById(gymId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
            notice.setGym(gym);
        }
        model.addAttribute("notice", notice);
        model.addAttribute("gyms", gymRepository.findAll());
        return "pages/admin/notices/create";
    }

    /**
     * Displays the edit form for an existing notice.
     *
     * @param id notice ID
     * @param model Spring model to pass data to the view
     * @return view name for the notice edit page
     */
    @GetMapping("/{id}/update")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        NoticeEntity notice = findNoticeOrThrow(id);
        model.addAttribute("notice", notice);
        model.addAttribute("gyms", gymRepository.findAll());
        return "pages/admin/notices/update";
    }

    /**
     * Handles the creation of a new notice. Validates input and either redisplays
     * the form with errors or saves the new notice.
     *
     * @param notice notice object submitted from the form
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the notice list or the form view if errors occur
     */
    @PostMapping
    public String createNotice(@Valid @ModelAttribute("notice") NoticeEntity notice, BindingResult result, RedirectAttributes redirectAttributes, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("gyms", gymRepository.findAll());
            return "pages/admin/notices/create";
        }

        noticeRepository.save(notice);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Notice created successfully!"
        ));

        return "redirect:/admin/notices";
    }

    /**
     * Updates an existing notice. Validates input and either redisplays
     * the form with errors or saves the changes.
     *
     * @param id notice ID
     * @param formNotice notice object submitted from the form
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the notice detail page or the form view if errors occur
     */
    @PutMapping("/{id}")
    public String updateNotice(
            @PathVariable("id") long id,
            @Valid @ModelAttribute("notice") NoticeEntity formNotice,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        NoticeEntity notice = findNoticeOrThrow(id);

        if (result.hasErrors()) {
            formNotice.setId(notice.getId());
            model.addAttribute("notice", formNotice);
            model.addAttribute("gyms", gymRepository.findAll());
            return "pages/admin/notices/update";
        }

        notice.setTitle(formNotice.getTitle());
        notice.setMessage(formNotice.getMessage());
        notice.setGym(formNotice.getGym());
        noticeRepository.save(notice);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Notice updated successfully!"
        ));

        return "redirect:/admin/notices/" + id;
    }

    /**
     * Deletes a notice by its ID.
     *
     * @param id notice ID
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the notice list
     */
    @DeleteMapping("/{id}")
    public String deleteNotice(@PathVariable("id") long id, RedirectAttributes redirectAttributes) {
        noticeRepository.deleteById(id);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success", 
            "message", "Notice deleted successfully!"
        ));

        return "redirect:/admin/notices";
    }

    /**
     * Finds a notice by ID or throws a ResponseStatusException if not found.
     *
     * @param noticeId notice ID
     * @return the notice entity
     * @throws ResponseStatusException if notice not found
     */
    private NoticeEntity findNoticeOrThrow(long noticeId) {
        return noticeRepository
                .findById(noticeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notice not found"));
    }
}
