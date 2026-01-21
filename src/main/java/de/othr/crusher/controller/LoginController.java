package de.othr.crusher.controller;

import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Controller for handling login page requests. */
@Controller
public class LoginController {

  /**
   * Displays the custom login page. Handles logout and error notifications via toast messages.
   *
   * @param logout optional parameter indicating successful logout
   * @param error optional parameter indicating login failure
   * @param redirectAttributes attributes for flash scope
   * @return the login view template or redirect
   */
  @GetMapping("/login")
  public String login(
      @RequestParam(required = false) String logout,
      @RequestParam(required = false) String error,
      @RequestParam(required = false) String deleted,
      RedirectAttributes redirectAttributes) {

    if (logout != null) {
      redirectAttributes.addFlashAttribute(
          "toast",
          Map.of(
              "type", "success",
              "title", "Logged out",
              "message", "You have been logged out successfully"));
      return "redirect:/login";
    }

    if (error != null) {
      redirectAttributes.addFlashAttribute(
          "toast",
          Map.of(
              "type", "error",
              "title", "Login failed",
              "message", "Invalid username or password"));
      return "redirect:/login";
    }

    if (deleted != null) {
      redirectAttributes.addFlashAttribute(
          "toast",
          Map.of(
              "type", "success",
              "title", "Account deleted",
              "message", "Your account has been deleted"));
      return "redirect:/login";
    }

    return "pages/login";
  }
}
