package de.othr.crusher.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling login page requests.
 */
@Controller
public class LoginController {

    /**
     * Displays the custom login page.
     *
     * @return the login view template
     */
    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }
}

