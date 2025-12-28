package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderCommentEntity;
import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderCommentRepository;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

/**
 * Controller for managing boulder comments.
 * Provides endpoints for creating user comments on boulders.
 */
@Controller
public class BoulderCommentController {

    private final BoulderCommentRepository commentRepository;
    private final BoulderRepository boulderRepository;
    private final UserRepository userRepository;

    public BoulderCommentController(
            BoulderCommentRepository commentRepository,
            BoulderRepository boulderRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.boulderRepository = boulderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new comment for a boulder.
     *
     * @param boulderId the ID of the boulder to comment on
     * @param comment the comment text
     * @param principal the authenticated user
     * @return redirect back to the boulder detail page
     */
    @PostMapping("/boulders/{boulderId}/comments")
    @Transactional
    public String createComment(
            @PathVariable("boulderId") Long boulderId,
            @RequestParam("comment") String comment,
            Principal principal) {
        UserEntity user = findUserByPrincipal(principal);
        BoulderEntity boulder = boulderRepository.findById(boulderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        if (comment == null || comment.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment text cannot be empty");
        }

        BoulderCommentEntity commentEntity = new BoulderCommentEntity(user, boulder, comment.trim());
        commentRepository.save(commentEntity);

        return "redirect:/boulders/" + boulderId;
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
