package de.othr.crusher.controller;

import de.othr.crusher.model.EventCommentEntity;
import de.othr.crusher.model.EventEntity;
import de.othr.crusher.model.EventRatingEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.EventCommentRepository;
import de.othr.crusher.repository.EventRatingRepository;
import de.othr.crusher.repository.EventRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * Controller for displaying event detail pages.
 */
@Controller
public class EventViewController {

    private final EventRepository eventRepository;
    private final EventCommentRepository eventCommentRepository;
    private final EventRatingRepository eventRatingRepository;
    private final UserRepository userRepository;

    public EventViewController(
            EventRepository eventRepository,
            EventCommentRepository eventCommentRepository,
            EventRatingRepository eventRatingRepository,
            UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.eventCommentRepository = eventCommentRepository;
        this.eventRatingRepository = eventRatingRepository;
        this.userRepository = userRepository;
    }

    /**
     * Displays details for a specific event.
     *
     * @param gymId the gym ID
     * @param eventId the event ID
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the event detail page
     */
    @GetMapping("/gyms/{gymId}/events/{eventId}")
    @Transactional(readOnly = true)
    public String showEvent(
            @PathVariable("gymId") Long gymId,
            @PathVariable("eventId") Long eventId,
            Principal principal,
            Model model) {
        UserEntity user = findUserByPrincipal(principal);
        EventEntity event = findEventInGymOrThrow(gymId, eventId);

        Integer currentRating = eventRatingRepository.findByUserIdAndEventId(user.getId(), eventId)
                .map(EventRatingEntity::getRating)
                .orElse(0);

        List<EventRatingEntity> allRatings = eventRatingRepository.findByEventId(eventId);
        Double averageRating = allRatings.isEmpty() ? null :
                allRatings.stream()
                        .mapToInt(EventRatingEntity::getRating)
                        .average()
                        .orElse(0.0);

        List<EventCommentEntity> comments = eventCommentRepository.findByEventIdOrderByCreatedAtDesc(eventId);

        model.addAttribute("event", event);
        model.addAttribute("gym", event.getGym());
        model.addAttribute("currentRating", currentRating);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("comments", comments);

        return "pages/gyms/events/detail";
    }

    private EventEntity findEventInGymOrThrow(Long gymId, Long eventId) {
        EventEntity event = eventRepository
                .findById(eventId)
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
