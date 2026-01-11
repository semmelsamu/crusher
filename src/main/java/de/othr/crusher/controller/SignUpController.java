package de.othr.crusher.controller;

import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.UserRepository;
import de.othr.crusher.utils.login.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for handling user registration.
 */
@Controller
public class SignUpController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public SignUpController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Displays the sign-up page.
     *
     * @return the sign-up view template
     */
    @GetMapping("/signup")
    public String signup() {
        return "pages/signup";
    }

    /**
     * Processes user registration.
     * Validates input, creates new user, and automatically logs them in.
     *
     * @param username the chosen username
     * @param password the chosen password
     * @param confirmPassword password confirmation
     * @param request the HTTP request for session management
     * @param model model for adding error messages
     * @param redirectAttributes attributes for flash scope
     * @return redirect to dashboard on success, or signup page on error
     */
    @PostMapping("/signup")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            HttpServletRequest request,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Validate password length (minimum 4 characters)
        if (password.length() < 4) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Registration failed",
                "message", "Password must be at least 4 characters long"
            ));
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/signup";
        }

        // Validate passwords match
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Registration failed",
                "message", "Passwords do not match"
            ));
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/signup";
        }

        // Check if username already exists
        if (userRepository.findByName(username).isPresent()) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Registration failed",
                "message", "Username already exists"
            ));
            redirectAttributes.addFlashAttribute("username", username);
            return "redirect:/signup";
        }

        // Create new user with BCrypt encoded password
        UserEntity newUser = new UserEntity();
        newUser.setName(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setRole("USER"); // Default role for new users
        userRepository.save(newUser);

        // Automatically log in the new user
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            password,
            userDetails.getAuthorities()
        );

        // Create a new security context and set the authentication
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Manually save the security context in the session
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        // Redirect to dashboard with success message
        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "title", "Account created",
            "message", "Welcome to crusher! Your account has been created successfully"
        ));
        return "redirect:/dashboard";
    }
}
