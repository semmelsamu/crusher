package de.othr.crusher.controller;

import de.othr.crusher.model.EventEntity;
import de.othr.crusher.model.EventRatingEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.EventRatingRepository;
import de.othr.crusher.repository.EventRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing event ratings.
 * Provides endpoints for creating and updating user ratings for events.
 */
@Controller
public class EventRatingController {

    private final EventRatingRepository ratingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventRatingController(
            EventRatingRepository ratingRepository,
            EventRepository eventRepository,
            UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates or updates a rating for an event.
     * This endpoint is idempotent - it will create a new rating if none exists,
     * or update the existing rating if one already exists for this user and event.
     *
     * @param gymId the ID of the gym
     * @param eventId the ID of the event to rate
     * @param rating the rating value (1-5)
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages
     * @return redirect back to the event detail page
     */
    @PostMapping("/gyms/{gymId}/events/{eventId}/rating")
    @Transactional
    public String setRating(
            @PathVariable("gymId") Long gymId,
            @PathVariable("eventId") Long eventId,
            @RequestParam("rating") Integer rating,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        EventEntity event = findEventInGymOrThrow(gymId, eventId);

        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        // Find existing rating or create new one
        EventRatingEntity ratingEntity = ratingRepository.findByUserIdAndEventId(user.getId(), eventId)
                .orElse(new EventRatingEntity());

        boolean isNewRating = ratingEntity.getId() == null;

        ratingEntity.setUser(user);
        ratingEntity.setEvent(event);
        ratingEntity.setRating(rating);

        ratingRepository.save(ratingEntity);

        // Add success toast
        Map<String, String> toast = new HashMap<>();
        toast.put("type", "success");
        toast.put("title", isNewRating ? "Rating added!" : "Rating updated!");
        toast.put("message", "You rated this event " + rating + " out of 5 stars.");
        redirectAttributes.addFlashAttribute("toast", toast);

        return "redirect:/gyms/" + gymId + "/events/" + eventId;
    }

    private EventEntity findEventInGymOrThrow(Long gymId, Long eventId) {
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
     * Helper method to find the current user from the security principal.
     *
     * @param principal the authenticated user principal
     * @return the UserEntity for the current user
     * @throws ResponseStatusException if the user is not found
     */
    private UserEntity findUserByPrincipal(Principal principal) {
        return userRepository.findByName(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
