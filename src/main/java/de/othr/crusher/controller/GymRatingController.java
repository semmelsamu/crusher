package de.othr.crusher.controller;

import de.othr.crusher.model.GymEntity;
import de.othr.crusher.model.GymRatingEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.GymRatingRepository;
import de.othr.crusher.repository.GymRepository;
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
 * Controller for managing gym ratings.
 * Provides endpoints for creating and updating user ratings for gyms.
 */
@Controller
public class GymRatingController {

    private final GymRatingRepository ratingRepository;
    private final GymRepository gymRepository;
    private final UserRepository userRepository;

    public GymRatingController(
        GymRatingRepository ratingRepository,
        GymRepository gymRepository,
        UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.gymRepository = gymRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates or updates a rating for a gym.
     * This endpoint is idempotent - it will create a new rating if none exists,
     * or update the existing rating if one already exists for this user and gym.
     *
     * @param gymId the ID of the gym to rate
     * @param rating the rating value (1-5)
     * @param principal the authenticated user
     * @param redirectAttributes attributes for flash messages
     * @return redirect back to the gym detail page
     */
    @PostMapping("/gyms/{gymId}/rating")
    @Transactional
    public String setRating(
        @PathVariable("gymId") Long gymId,
        @RequestParam("rating") Integer rating,
        Principal principal,
        RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        GymEntity gym = gymRepository.findByIdAndDeletedFalse(gymId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gym not found"));

        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        // Find existing rating or create new one
        GymRatingEntity ratingEntity = ratingRepository.findByUserIdAndGymId(user.getId(), gymId)
                                       .orElse(new GymRatingEntity());

        boolean isNewRating = ratingEntity.getId() == null;

        ratingEntity.setUser(user);
        ratingEntity.setGym(gym);
        ratingEntity.setRating(rating);

        ratingRepository.save(ratingEntity);

        // Add success toast
        Map<String, String> toast = new HashMap<>();
        toast.put("type", "success");
        toast.put("title", isNewRating ? "Rating added!" : "Rating updated!");
        toast.put("message", "You rated this gym " + rating + " out of 5 stars.");
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
        return userRepository.findByName(principal.getName())
               .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
