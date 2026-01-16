package de.othr.crusher.controller;

import de.othr.crusher.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
