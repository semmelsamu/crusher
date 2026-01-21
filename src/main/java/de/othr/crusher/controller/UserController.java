package de.othr.crusher.controller;

import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.UserRepository;
import de.othr.crusher.utils.login.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for managing users.
 * Provides endpoints for listing users and editing user accounts.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public UserController(
            UserRepository userRepository,
            CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Displays a list of all users with their roles (admin only).
     *
     * @param principal the authenticated user principal
     * @param model Spring model to pass data to the view
     * @return view name for the users overview page
     */
    @GetMapping
    public String showAllUsers(Principal principal, Model model) {
        if (!isAdmin(principal)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        model.addAttribute("users", userRepository.findAll());
        return "pages/admin/users/all";
    }

    /**
     * Displays the edit form for a specific user.
     * Users can edit their own account, admins can edit any user.
     *
     * @param userId identifier of the user to edit
     * @param principal the authenticated user principal
     * @param model Spring model to pass data to the view
     * @param request HTTP request for referer handling
     * @return view name for the account edit page
     */
    @GetMapping("/{userId}/edit")
    public String showEditForm(
            @PathVariable("userId") Long userId,
            Principal principal,
            Model model,
            HttpServletRequest request) {
        
        if (!canEditUser(principal, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        UserEntity currentUser = findUserByPrincipal(principal);
        boolean admin = isAdmin(principal);
        
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", admin);
        model.addAttribute("isSelf", currentUser.getId().equals(userId));
        model.addAttribute("returnTo", resolveReturnTo(null, request, admin));
        return "pages/account";
    }

    /**
     * Updates a user.
     * Regular users can update their own username and email.
     * Admins can update any user's username, email, and role.
     *
     * @param userId identifier of the user to update
     * @param formUser user payload from the form
     * @param result validation result
     * @param principal the authenticated user principal
     * @param request HTTP request for session management
     * @param redirectAttributes attributes for flash messages on redirect
     * @return redirect to appropriate page
     */
    @PutMapping("/{userId}")
    public String updateUser(
            @PathVariable("userId") Long userId,
            @Valid @ModelAttribute("user") UserEntity formUser,
            BindingResult result,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        
        if (!canEditUser(principal, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        UserEntity currentUser = findUserByPrincipal(principal);
        boolean admin = isAdmin(principal);
        boolean isSelf = currentUser.getId().equals(userId);
        
        String trimmedUsername = formUser.getName() == null ? "" : formUser.getName().trim();
        String trimmedEmail = formUser.getEmail() == null ? "" : formUser.getEmail().trim();

        if (!StringUtils.hasText(trimmedUsername) || !StringUtils.hasText(trimmedEmail)) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Username and email are required"
            ));
            return "redirect:/users/" + userId + "/edit";
        }

        // Check for duplicate username
        Optional<UserEntity> existingUser = userRepository.findByName(trimmedUsername);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Username already exists"
            ));
            return "redirect:/users/" + userId + "/edit";
        }

        // Check for duplicate email
        Optional<UserEntity> existingEmail = userRepository.findByEmail(trimmedEmail);
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Email address already registered"
            ));
            return "redirect:/users/" + userId + "/edit";
        }

        // Update user fields
        user.setName(trimmedUsername);
        user.setEmail(trimmedEmail);
        
        // Only admins can change roles
        if (admin && formUser.getRole() != null) {
            String trimmedRole = formUser.getRole().trim();
            if (StringUtils.hasText(trimmedRole)) {
                user.setRole(trimmedRole);
            }
        }
        
        userRepository.save(user);

        // Refresh authentication if user edited their own account
        if (isSelf) {
            refreshAuthentication(user, request);
        }

        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "message", "User updated successfully!"
        ));

        // Redirect based on who made the edit
        if (admin && !isSelf) {
            return "redirect:/users";
        } else {
            return "redirect:/dashboard";
        }
    }

    /**
     * Deletes a user account (soft delete).
     * Users can only delete their own account.
     *
     * @param userId identifier of the user to delete
     * @param principal the authenticated user principal
     * @param request HTTP request
     * @param response HTTP response
     * @return redirect to login page
     */
    @DeleteMapping("/{userId}")
    public String deleteUser(
            @PathVariable("userId") Long userId,
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        UserEntity currentUser = findUserByPrincipal(principal);
        
        // Only allow users to delete their own account
        if (!currentUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own account");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        user.setDeleted(true);
        userRepository.save(user);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return "redirect:/login?deleted=1";
    }

    /**
     * Checks if the current user has admin privileges.
     *
     * @param principal the authenticated user principal
     * @return true if user has ADMIN, OWNER, or SETTER role
     */
    private boolean isAdmin(Principal principal) {
        if (principal == null) {
            return false;
        }
        
        UserEntity user = userRepository.findByNameAndDeletedFalse(principal.getName())
                .orElse(null);
        
        if (user == null) {
            return false;
        }
        
        String role = user.getRole();
        return "ADMIN".equals(role) || "OWNER".equals(role) || "SETTER".equals(role);
    }

    /**
     * Checks if the current user can edit the specified user.
     * Users can edit themselves, admins can edit anyone.
     *
     * @param principal the authenticated user principal
     * @param userId the ID of the user to be edited
     * @return true if the user can edit
     */
    private boolean canEditUser(Principal principal, Long userId) {
        if (principal == null) {
            return false;
        }
        
        UserEntity currentUser = findUserByPrincipal(principal);
        return isAdmin(principal) || currentUser.getId().equals(userId);
    }

    /**
     * Finds a user by their principal.
     *
     * @param principal the authenticated user principal
     * @return the UserEntity
     * @throws ResponseStatusException if user not found
     */
    private UserEntity findUserByPrincipal(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        return userRepository.findByNameAndDeletedFalse(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    /**
     * Refreshes the authentication context after user details are updated.
     *
     * @param user the updated user entity
     * @param request HTTP request for session management
     */
    private void refreshAuthentication(UserEntity user, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getName());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context);
        }
    }

    /**
     * Resolves where to redirect after an operation.
     *
     * @param returnTo explicit return path
     * @param request HTTP request
     * @param isAdmin whether the current user is an admin
     * @return the resolved return path
     */
    private String resolveReturnTo(String returnTo, HttpServletRequest request, boolean isAdmin) {
        String resolved = sanitizeReturnTo(returnTo);
        if (!StringUtils.hasText(resolved)) {
            resolved = sanitizeReturnTo(extractPathFromReferer(request.getHeader("Referer")));
        }
        if (!StringUtils.hasText(resolved) || resolved.startsWith("/users/") && resolved.endsWith("/edit")) {
            return isAdmin ? "/users" : "/dashboard";
        }
        return resolved;
    }

    /**
     * Extracts the path from a referer URL.
     *
     * @param referer the referer URL
     * @return the extracted path
     */
    private String extractPathFromReferer(String referer) {
        if (!StringUtils.hasText(referer)) {
            return null;
        }

        try {
            URI uri = new URI(referer);
            String path = uri.getPath();
            if (!StringUtils.hasText(path)) {
                return null;
            }
            String query = uri.getQuery();
            return StringUtils.hasText(query) ? path + "?" + query : path;
        } catch (URISyntaxException ex) {
            return null;
        }
    }

    /**
     * Sanitizes a return path to prevent open redirects.
     *
     * @param returnTo the return path to sanitize
     * @return the sanitized path or null
     */
    private String sanitizeReturnTo(String returnTo) {
        if (!StringUtils.hasText(returnTo)) {
            return null;
        }
        if (!returnTo.startsWith("/") || returnTo.startsWith("//") || returnTo.contains("://")) {
            return null;
        }
        return returnTo;
    }
}
