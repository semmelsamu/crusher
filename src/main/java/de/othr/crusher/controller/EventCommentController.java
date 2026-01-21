package de.othr.crusher.controller;

import de.othr.crusher.model.EventCommentEntity;
import de.othr.crusher.model.EventEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.EventCommentRepository;
import de.othr.crusher.repository.EventRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing event comments.
 * Provides endpoints for creating user comments on events.
 */
@Controller
public class EventCommentController {

    private final EventCommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventCommentController(
            EventCommentRepository commentRepository,
            EventRepository eventRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new comment for an event.
     *
     * @param gymId the ID of the gym
     * @param eventId the ID of the event to comment on
     * @param comment the comment text
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages
     * @return redirect back to the event detail page
     */
    @PostMapping("/gyms/{gymId}/events/{eventId}/comments")
    @Transactional
    public String createComment(
            @PathVariable("gymId") Long gymId,
            @PathVariable("eventId") Long eventId,
            @RequestParam("comment") String comment,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        EventEntity event = findEventInGymOrThrow(gymId, eventId);

        if (comment == null || comment.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment text cannot be empty");
        }

        EventCommentEntity commentEntity = new EventCommentEntity(user, event, comment.trim());
        commentRepository.save(commentEntity);

        // Add success toast
        Map<String, String> toast = new HashMap<>();
        toast.put("type", "success");
        toast.put("title", "Comment posted!");
        toast.put("message", "Your comment has been successfully added.");
        redirectAttributes.addFlashAttribute("toast", toast);

        return "redirect:/gyms/" + gymId + "/events/" + eventId;
    }

    /**
     * Deletes a comment.
     * Only the comment owner can delete their own comment.
     *
     * @param gymId the ID of the gym
     * @param eventId the ID of the event
     * @param commentId the ID of the comment to delete
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages
     * @return redirect back to the event detail page
     */
    @DeleteMapping("/gyms/{gymId}/events/{eventId}/comments/{commentId}")
    @Transactional
    public String deleteComment(
            @PathVariable("gymId") Long gymId,
            @PathVariable("eventId") Long eventId,
            @PathVariable("commentId") Long commentId,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        EventCommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        // Ensure the comment belongs to the current user or user is admin
        boolean isAdmin = "ADMIN".equals(user.getRole());
        if (!comment.getUser().getId().equals(user.getId()) && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own comments");
        }

        // Ensure the comment belongs to the specified event and gym
        if (!comment.getEvent().getId().equals(eventId)
                || !comment.getEvent().getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to this event");
        }

        commentRepository.delete(comment);

        // Add success toast
        Map<String, String> toast = new HashMap<>();
        toast.put("type", "success");
        toast.put("title", "Comment deleted!");
        toast.put("message", "Your comment has been successfully deleted.");
        redirectAttributes.addFlashAttribute("toast", toast);

        return "redirect:/gyms/" + gymId + "/events/" + eventId;
    }

    /**
     * Updates a comment.
     * Only the comment owner can update their own comment.
     *
     * @param gymId the ID of the gym
     * @param eventId the ID of the event
     * @param commentId the ID of the comment to update
     * @param newComment the new comment text
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages
     * @return redirect back to the event detail page
     */
    @PutMapping("/gyms/{gymId}/events/{eventId}/comments/{commentId}")
    @Transactional
    public String updateComment(
            @PathVariable("gymId") Long gymId,
            @PathVariable("eventId") Long eventId,
            @PathVariable("commentId") Long commentId,
            @RequestParam("comment") String newComment,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        EventCommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        // Ensure the comment belongs to the current user
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only edit your own comments");
        }

        // Ensure the comment belongs to the specified event and gym
        if (!comment.getEvent().getId().equals(eventId)
                || !comment.getEvent().getGym().getId().equals(gymId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment does not belong to this event");
        }

        if (newComment == null || newComment.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment text cannot be empty");
        }

        comment.setComment(newComment.trim());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        // Add success toast
        Map<String, String> toast = new HashMap<>();
        toast.put("type", "success");
        toast.put("title", "Comment updated!");
        toast.put("message", "Your comment has been successfully updated.");
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
