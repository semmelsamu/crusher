package de.othr.crusher.controller;

import de.othr.crusher.model.EventEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.EventRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.service.EmailService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing events within a gym in the admin area.
 */
@Controller
@RequestMapping("/admin/gyms/{gymId}/events")
public class EventController {

    private final EventRepository eventRepository;
    private final GymRepository gymRepository;
    private final SessionRepository sessionRepository;
    private final EmailService emailService;

    /**
     * Creates a new EventController with the given repositories.
     *
     * @param eventRepository repository for accessing event data
     * @param gymRepository repository for accessing gym data
     */
    public EventController(EventRepository eventRepository, GymRepository gymRepository,
            SessionRepository sessionRepository, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.gymRepository = gymRepository;
        this.sessionRepository = sessionRepository;
        this.emailService = emailService;
    }

    /**
     * Displays the edit form for an existing event.
     *
     * @param gymId identifier of the parent gym
     * @param eventId identifier of the event
     * @param model Spring model to pass data to the view
     * @return view name for the event edit page
     */
    @GetMapping("/{eventId}/update")
    public String showEditForm(
            @PathVariable("gymId") long gymId,
            @PathVariable("eventId") long eventId,
            Model model) {
        EventEntity event = findEventInGymOrThrow(gymId, eventId);
        model.addAttribute("gym", event.getGym());
        model.addAttribute("event", event);
        return "pages/admin/gyms/events/update";
    }

    /**
     * Displays the form for creating a new event.
     *
     * @param gymId identifier of the parent gym
     * @param model Spring model to pass data to the view
     * @return view name for the event creation page
     */
    @GetMapping("/create")
    public String showCreateForm(@PathVariable("gymId") long gymId, Model model) {
        GymEntity gym = findGymOrThrow(gymId);
        EventEntity event = new EventEntity();
        event.setGym(gym);

        model.addAttribute("gym", gym);
        model.addAttribute("event", event);
        return "pages/admin/gyms/events/create";
    }

    /**
     * Handles the creation of a new event. Validates input and either redisplays
     * the form with errors or saves the new event.
     *
     * @param gymId identifier of the parent gym
     * @param formEvent event object submitted from the form
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the gym detail page or the form view if errors occur
     */
    @PostMapping
    public String createEvent(
            @PathVariable("gymId") long gymId,
            @Valid @ModelAttribute("event") EventEntity formEvent,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        GymEntity gym = findGymOrThrow(gymId);

        validateSchedule(formEvent, result);

        if (result.hasErrors()) {
            model.addAttribute("gym", gym);
            model.addAttribute("event", formEvent);
            return "pages/admin/gyms/events/create";
        }

        normalizeSchedule(formEvent);
        formEvent.setGym(gym);
        EventEntity savedEvent = eventRepository.save(formEvent);

        List<UserEntity> users = sessionRepository.findDistinctUsersByGymId(gymId);
        if (!users.isEmpty()) {
            for (UserEntity user : users) {
                try {
                    emailService.sendNewEventEmail(user.getEmail(), user.getName(), gym.getName(), savedEvent);
                } catch (Exception e) {
                    System.err.println("Failed to send event email to " + user.getEmail() + ": " + e.getMessage());
                }
            }
        }

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "message", "Event created successfully!"
        ));

        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Updates an existing event. Validates input and either redisplays
     * the form with errors or saves the changes.
     *
     * @param gymId identifier of the parent gym
     * @param eventId identifier of the event
     * @param formEvent event object submitted from the form
     * @param result validation result
     * @param redirectAttributes attributes for flash messages on redirect
     * @param model Spring model for re-rendering the form if needed
     * @return redirect to the gym detail page or the form view if errors occur
     */
    @PutMapping("/{eventId}")
    public String updateEvent(
            @PathVariable("gymId") long gymId,
            @PathVariable("eventId") long eventId,
            @Valid @ModelAttribute("event") EventEntity formEvent,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {
        EventEntity event = findEventInGymOrThrow(gymId, eventId);

        validateSchedule(formEvent, result);

        if (result.hasErrors()) {
            formEvent.setId(event.getId());
            formEvent.setGym(event.getGym());
            model.addAttribute("gym", event.getGym());
            model.addAttribute("event", formEvent);
            return "pages/admin/gyms/events/update";
        }

        event.setTitle(formEvent.getTitle());
        event.setDescription(formEvent.getDescription());
        event.setPeriodic(formEvent.isPeriodic());
        event.setWeekday(formEvent.getWeekday());
        event.setDate(formEvent.getDate());
        event.setFrequency(formEvent.getFrequency());
        event.setTime(formEvent.getTime());
        normalizeSchedule(event);
        eventRepository.save(event);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "message", "Event updated successfully!"
        ));

        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Soft-deletes an event by setting its deleted flag.
     *
     * @param gymId identifier of the parent gym
     * @param eventId identifier of the event
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the gym detail page
     */
    @DeleteMapping("/{eventId}")
    public String deleteEvent(
            @PathVariable("gymId") long gymId,
            @PathVariable("eventId") long eventId,
            RedirectAttributes redirectAttributes) {
        EventEntity event = findEventInGymOrThrow(gymId, eventId);
        event.setDeleted(true);
        eventRepository.save(event);

        // Add success message for toast notification
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "message", "Event deleted successfully!"
        ));

        return "redirect:/admin/gyms/" + gymId;
    }

    /**
     * Finds an event by ID and validates it belongs to the specified gym.
     *
     * @param gymId identifier of the parent gym
     * @param eventId identifier of the event
     * @return the event entity
     * @throws ResponseStatusException if event not found or doesn't belong to gym
     */
    private EventEntity findEventInGymOrThrow(long gymId, long eventId) {
        EventEntity event = eventRepository
                .findByIdAndDeletedFalse(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (event.getGym() == null || event.getGym().getId() == null
                || !event.getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event does not belong to gym");
        }
        return event;
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
                .findByIdAndDeletedFalse(gymId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));
    }

    private void validateSchedule(EventEntity event, BindingResult result) {
        if (event.isPeriodic()) {
            if (event.getWeekday() == null) {
                result.rejectValue("weekday", "NotNull", "Please select a weekday");
            }
            if (event.getFrequency() == null) {
                result.rejectValue("frequency", "NotNull", "Please select a frequency");
            }
        } else if (event.getDate() == null) {
            result.rejectValue("date", "NotNull", "Please enter a date");
        }
    }

    private void normalizeSchedule(EventEntity event) {
        if (event.isPeriodic()) {
            event.setDate(null);
        } else {
            event.setWeekday(null);
            event.setFrequency(null);
        }
    }

}
