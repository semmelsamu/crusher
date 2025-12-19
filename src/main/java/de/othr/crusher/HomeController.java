package de.othr.crusher;

import de.othr.crusher.model.SessionEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Optional;

/**
 * Controller for the home page.
 * Provides quick access to start a new session or continue an active one.
 */
@Controller
public class HomeController {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public HomeController(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Displays the home page with quick session actions.
     * Shows either:
     * - A link to the active session if one exists
     * - A quick-start button for the last used gym if no active session
     *
     * @param principal the authenticated user
     * @param model Spring model to pass data to the view
     * @return view name for the home page
     */
    @GetMapping("/")
    @Transactional(readOnly = true)
    public String showHome(Principal principal, Model model) {
        UserEntity user = findUserByPrincipal(principal);

        // Check for active session
        Optional<SessionEntity> activeSession = sessionRepository.findByUserIdAndEndedAtIsNull(user.getId());

        if (activeSession.isPresent()) {
            model.addAttribute("activeSession", activeSession.get());
        } else {
            Optional<SessionEntity> lastSession = sessionRepository.findByUserIdOrderByStartedAtDesc(user.getId())
                    .stream()
                    .findFirst();

            lastSession.ifPresent(session -> model.addAttribute("lastGym", session.getGym()));
        }
        
        throw new RuntimeException("Test error");

        //return "pages/home";
    }

    /**
     * Displays the components demo page.
     *
     * @return view name for the components page
     */
    @GetMapping("/components")
    public String showComponents() {
        return "pages/components";
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
