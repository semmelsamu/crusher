package de.othr.crusher.controller;

import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.BoulderRatingEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderRatingRepository;
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
 * Controller for managing boulder ratings.
 * Provides endpoints for creating and updating user ratings for boulders.
 */
@Controller
public class BoulderRatingController {

    private final BoulderRatingRepository ratingRepository;
    private final BoulderRepository boulderRepository;
    private final UserRepository userRepository;

    public BoulderRatingController(
            BoulderRatingRepository ratingRepository,
            BoulderRepository boulderRepository,
            UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.boulderRepository = boulderRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates or updates a rating for a boulder.
     * This endpoint is idempotent - it will create a new rating if none exists,
     * or update the existing rating if one already exists for this user and boulder.
     *
     * @param boulderId the ID of the boulder to rate
     * @param rating the rating value (1-5)
     * @param principal the authenticated user
     * @return redirect back to the boulder detail page
     */
    @PostMapping("/boulders/{boulderId}/rating")
    @Transactional
    public String setRating(
            @PathVariable("boulderId") Long boulderId,
            @RequestParam("rating") Integer rating,
            Principal principal) {
        UserEntity user = findUserByPrincipal(principal);
        BoulderEntity boulder = boulderRepository.findById(boulderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boulder not found"));

        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        // Find existing rating or create new one
        BoulderRatingEntity ratingEntity = ratingRepository.findByUserIdAndBoulderId(user.getId(), boulderId)
                .orElse(new BoulderRatingEntity());

        ratingEntity.setUser(user);
        ratingEntity.setBoulder(boulder);
        ratingEntity.setRating(rating);

        ratingRepository.save(ratingEntity);

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
