package de.othr.crusher.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.UserRepository;
import de.othr.crusher.utils.login.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AccountController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    public AccountController(
            UserRepository userRepository,
            CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/account")
    public String showAccount(Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);
        model.addAttribute("currentUser", user);
        return "pages/account";
    }

    @PutMapping("/account")
    public String updateAccount(
            @RequestParam String username,
            @RequestParam String email,
            Principal principal,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        UserEntity user = findUserByPrincipal(principal);
        String trimmedUsername = username == null ? "" : username.trim();
        String trimmedEmail = email == null ? "" : email.trim();

        if (!StringUtils.hasText(trimmedUsername) || !StringUtils.hasText(trimmedEmail)) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Username and email are required"
            ));
            return redirectBack(request);
        }

        Optional<UserEntity> existingUser = userRepository.findByName(trimmedUsername);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Username already exists"
            ));
            return redirectBack(request);
        }

        Optional<UserEntity> existingEmail = userRepository.findByEmail(trimmedEmail);
        if (existingEmail.isPresent() && !existingEmail.get().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("toast", Map.of(
                "type", "error",
                "title", "Update failed",
                "message", "Email address already registered"
            ));
            return redirectBack(request);
        }

        user.setName(trimmedUsername);
        user.setEmail(trimmedEmail);
        userRepository.save(user);

        refreshAuthentication(user, request);

        redirectAttributes.addFlashAttribute("toast", Map.of(
            "type", "success",
            "message", "Account updated successfully!"
        ));

        return redirectBack(request);
    }

    @DeleteMapping("/account")
    public String deleteAccount(
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response) {
        UserEntity user = findUserByPrincipal(principal);
        user.setDeleted(true);
        userRepository.save(user);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        new SecurityContextLogoutHandler().logout(request, response, authentication);

        return "redirect:/login?deleted=1";
    }

    private UserEntity findUserByPrincipal(Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        return userRepository.findByNameAndDeletedFalse(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

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

    private String redirectBack(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (StringUtils.hasText(referer)) {
            return "redirect:" + referer;
        }
        return "redirect:/dashboard";
    }
}
