package de.othr.crusher.controller;

import de.othr.crusher.model.GymCommentEntity;
import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.GymCommentRepository;
import de.othr.crusher.repository.GymRepository;
import de.othr.crusher.repository.UserRepository;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

/** Controller for managing gym comments. Provides endpoints for creating user comments on gyms. */
@Controller
public class GymCommentController {

  private final GymCommentRepository commentRepository;
  private final GymRepository gymRepository;
  private final UserRepository userRepository;

  public GymCommentController(
      GymCommentRepository commentRepository,
      GymRepository gymRepository,
      UserRepository userRepository) {
    this.commentRepository = commentRepository;
    this.gymRepository = gymRepository;
    this.userRepository = userRepository;
  }

  /**
   * Creates a new comment for a gym.
   *
   * @param gymId the ID of the gym to comment on
   * @param comment the comment text
   * @param principal the authenticated user
   * @param redirectAttributes attributes for flash messages
   * @return redirect back to the gym detail page
   */
  @PostMapping("/gyms/{gymId}/comments")
  @Transactional
  public String createComment(
      @PathVariable("gymId") Long gymId,
      @RequestParam("comment") String comment,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    UserEntity user = findUserByPrincipal(principal);
    GymEntity gym =
        gymRepository
            .findByIdAndDeletedFalse(gymId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

    if (comment == null || comment.trim().isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment text cannot be empty");
    }

    GymCommentEntity commentEntity = new GymCommentEntity(user, gym, comment.trim());
    commentRepository.save(commentEntity);

    // Add success toast
    Map<String, String> toast = new HashMap<>();
    toast.put("type", "success");
    toast.put("title", "Comment posted!");
    toast.put("message", "Your comment has been successfully added.");
    redirectAttributes.addFlashAttribute("toast", toast);

    return "redirect:/gyms/" + gymId;
  }

  /**
   * Deletes a comment. Only the comment owner can delete their own comment.
   *
   * @param gymId the ID of the gym
   * @param commentId the ID of the comment to delete
   * @param principal the authenticated user
   * @param redirectAttributes attributes for flash messages
   * @return redirect back to the gym detail page
   */
  @DeleteMapping("/gyms/{gymId}/comments/{commentId}")
  @Transactional
  public String deleteComment(
      @PathVariable("gymId") Long gymId,
      @PathVariable("commentId") Long commentId,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    UserEntity user = findUserByPrincipal(principal);
    GymCommentEntity comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

    // Ensure the comment belongs to the current user or user is admin
    boolean isAdmin = "ADMIN".equals(user.getRole());
    if (!comment.getUser().getId().equals(user.getId()) && !isAdmin) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You can only delete your own comments");
    }

    // Ensure the comment belongs to the specified gym
    if (!comment.getGym().getId().equals(gymId)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Comment does not belong to this gym");
    }

    commentRepository.delete(comment);

    // Add success toast
    Map<String, String> toast = new HashMap<>();
    toast.put("type", "success");
    toast.put("title", "Comment deleted!");
    toast.put("message", "Your comment has been successfully deleted.");
    redirectAttributes.addFlashAttribute("toast", toast);

    return "redirect:/gyms/" + gymId;
  }

  /**
   * Updates a comment. Only the comment owner can update their own comment.
   *
   * @param gymId the ID of the gym
   * @param commentId the ID of the comment to update
   * @param newComment the new comment text
   * @param principal the authenticated user
   * @param redirectAttributes attributes for flash messages
   * @return redirect back to the gym detail page
   */
  @PutMapping("/gyms/{gymId}/comments/{commentId}")
  @Transactional
  public String updateComment(
      @PathVariable("gymId") Long gymId,
      @PathVariable("commentId") Long commentId,
      @RequestParam("comment") String newComment,
      Principal principal,
      RedirectAttributes redirectAttributes) {
    UserEntity user = findUserByPrincipal(principal);
    GymCommentEntity comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

    // Ensure the comment belongs to the current user
    if (!comment.getUser().getId().equals(user.getId())) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "You can only edit your own comments");
    }

    // Ensure the comment belongs to the specified gym
    if (!comment.getGym().getId().equals(gymId)) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Comment does not belong to this gym");
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

    return "redirect:/gyms/" + gymId;
  }

  /**
   * Helper method to find the current user from the security principal.
   *
   * @param principal the authenticated user principal
   * @return the UserEntity for the current user
   * @throws ResponseStatusException if the user is not found
   */
  private UserEntity findUserByPrincipal(Principal principal) {
    return userRepository
        .findByName(principal.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }
}
