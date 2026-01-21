package de.othr.crusher.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import de.othr.crusher.repository.UserRepository;

@ControllerAdvice
public class CurrentUserAdvice {

    private final UserRepository userRepository;

    public CurrentUserAdvice(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @ModelAttribute
    public void addCurrentUser(Model model, Principal principal) {
        if (principal == null) {
            return;
        }

        userRepository.findByNameAndDeletedFalse(principal.getName())
                .ifPresent(user -> model.addAttribute("currentUser", user));
    }
}
