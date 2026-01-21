package de.othr.crusher.controller;

import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing users in the admin area.
 * Provides endpoints for listing all users and their roles.
 */
@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final UserRepository userRepository;

    /**
     * Creates a new UserController with the given repository.
     *
     * @param userRepository repository for accessing user data
     */
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Displays a list of all users with their roles.
     *
     * @param model Spring model to pass data to the view
     * @return view name for the users overview page
     */
    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "pages/admin/users/all";
    }

    /**
     * Displays the edit form for a specific user.
     *
     * @param userId identifier of the user to edit
     * @param model Spring model to pass data to the view
     * @return view name for the account edit page
     */
    @GetMapping("/{userId}/edit")
    public String showEditForm(@PathVariable("userId") Long userId, Model model) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        model.addAttribute("currentUser", user);
        model.addAttribute("isAdminEdit", true);
        model.addAttribute("returnTo", "/admin/users");
        return "pages/account";
    }

    /**
     * Updates a user as an admin.
     *
     * @param userId identifier of the user to update
     * @param username new username
     * @param email new email
     * @param role new role
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to the users list page
     */
    @PutMapping("/{userId}")
    public String updateUser(
            @PathVariable("userId") Long userId,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String role,
            RedirectAttributes redirectAttributes) {
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        String trimmedUsername = username == null ? "" : username.trim();
        String trimmedEmail = email == null ? "" : email.trim();
        String trimmedRole = role == null ? "" : role.trim();

        if (!StringUtils.hasText(trimmedUsername) || !StringUtils.hasText(trimmedEmail) || !StringUtils.hasText(trimmedRole)) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Username, email, and role are required"
            ));
            return "redirect:/admin/users/" + userId + "/edit";
        }

        Optional<UserEntity> existingUser = userRepository.findByName(trimmedUsername);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Username already exists"
            ));
            return "redirect:/admin/users/" + userId + "/edit";
        }

        Optional<UserEntity> existingEmail = userRepository.findByEmail(trimmedEmail);
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Email address already registered"
            ));
            return "redirect:/admin/users/" + userId + "/edit";
        }

        user.setName(trimmedUsername);
        user.setEmail(trimmedEmail);
        user.setRole(trimmedRole);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "message", "User updated successfully!"
        ));

        return "redirect:/admin/users";
    }
}
