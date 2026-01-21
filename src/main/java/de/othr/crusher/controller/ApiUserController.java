package de.othr.crusher.controller;

import de.othr.crusher.dto.api.ApiErrorResponse;
import de.othr.crusher.dto.api.CreateUserRequest;
import de.othr.crusher.dto.api.UpdateUserRequest;
import de.othr.crusher.dto.api.UserResponse;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    private static final Set<String> PRIVILEGED_ROLES = Set.of("ROLE_ADMIN", "ROLE_OWNER");
    private static final Set<String> ALLOWED_USER_ROLES = Set.of("USER", "SETTER", "OWNER", "ADMIN");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ApiUserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<?> listUsers(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        if (!isPrivileged(authentication)) {
            return accessDenied();
        }

        List<UserResponse> users = userRepository.findAll().stream()
                .map(this::toUserResponse)
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        UserEntity user = findUserByAuthentication(authentication);
        return ResponseEntity.ok(toUserResponse(user));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getUser(
            @PathVariable("id") Long id,
            Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        UserEntity currentUser = findUserByAuthentication(authentication);
        if (!isPrivileged(authentication) && !currentUser.getId().equals(id)) {
            return accessDenied();
        }

        UserEntity user = currentUser.getId().equals(id)
                ? currentUser
                : userRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return ResponseEntity.ok(toUserResponse(user));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createUser(
            @RequestBody(required = false) CreateUserRequest request,
            Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        if (!isPrivileged(authentication)) {
            return accessDenied();
        }

        if (request == null) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Request body is required"));
        }

        String username = normalize(request.username());
        if (username == null) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Username is required"));
        }

        String email = normalize(request.email());
        if (email == null) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Email is required"));
        }

        if (request.password() == null || request.password().isBlank()) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Password is required"));
        }

        if (userRepository.findByName(username).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Username already exists"));
        }

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Email already exists"));
        }

        String role = normalizeRole(request.role());
        if (role == null) {
            role = "USER";
        }

        UserEntity user = new UserEntity();
        user.setName(username);
        user.setEmail(email);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(toUserResponse(user));
    }

    @PutMapping("/me")
    @Transactional
    public ResponseEntity<?> updateCurrentUser(
            @RequestBody(required = false) UpdateUserRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        if (request == null) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Request body is required"));
        }

        UserEntity user = findUserByAuthentication(authentication);
        return updateUser(user, request, false, httpRequest);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateUser(
            @PathVariable("id") Long id,
            @RequestBody(required = false) UpdateUserRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        if (request == null) {
            return ResponseEntity.badRequest().body(new ApiErrorResponse("Request body is required"));
        }

        UserEntity currentUser = findUserByAuthentication(authentication);
        boolean privileged = isPrivileged(authentication);
        if (!privileged && !currentUser.getId().equals(id)) {
            return accessDenied();
        }

        UserEntity user = currentUser.getId().equals(id)
                ? currentUser
                : userRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return updateUser(user, request, privileged, currentUser.getId().equals(id) ? httpRequest : null);
    }

    @DeleteMapping("/me")
    @Transactional
    public ResponseEntity<?> deleteCurrentUser(
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        UserEntity user = findUserByAuthentication(authentication);
        ResponseEntity<?> result = deleteUserInternal(user);
        if (result.getStatusCode().is2xxSuccessful()) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return result;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteUser(
            @PathVariable("id") Long id,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response) {
        if (!isAuthenticated(authentication)) {
            return notAuthenticated();
        }

        UserEntity currentUser = findUserByAuthentication(authentication);
        boolean privileged = isPrivileged(authentication);
        if (!privileged && !currentUser.getId().equals(id)) {
            return accessDenied();
        }

        UserEntity user = currentUser.getId().equals(id)
                ? currentUser
                : userRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ResponseEntity<?> result = deleteUserInternal(user);
        if (result.getStatusCode().is2xxSuccessful() && currentUser.getId().equals(id)) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return result;
    }

    private ResponseEntity<?> updateUser(
            UserEntity user,
            UpdateUserRequest request,
            boolean allowRoleChange,
            HttpServletRequest httpRequest) {
        if (request.username() != null) {
            String username = normalize(request.username());
            if (username == null) {
                return ResponseEntity.badRequest().body(new ApiErrorResponse("Username cannot be blank"));
            }

            userRepository.findByName(username)
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
                    });

            user.setName(username);
        }

        if (request.email() != null) {
            String email = normalize(request.email());
            if (email == null) {
                return ResponseEntity.badRequest().body(new ApiErrorResponse("Email cannot be blank"));
            }

            userRepository.findByEmail(email)
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
                    });

            user.setEmail(email);
        }

        if (request.password() != null) {
            if (request.password().isBlank()) {
                return ResponseEntity.badRequest().body(new ApiErrorResponse("Password cannot be blank"));
            }
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.role() != null) {
            if (!allowRoleChange) {
                return accessDenied();
            }

            String role = normalizeRole(request.role());
            if (role == null) {
                return ResponseEntity.badRequest().body(new ApiErrorResponse("Role cannot be blank"));
            }
            user.setRole(role);
        }

        userRepository.save(user);

        if (httpRequest != null) {
            refreshSession(user, httpRequest);
        }

        return ResponseEntity.ok(toUserResponse(user));
    }

    private ResponseEntity<?> deleteUserInternal(UserEntity user) {
        try {
            userRepository.delete(user);
            userRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ApiErrorResponse("User has related data and cannot be deleted"));
        }

        return ResponseEntity.noContent().build();
    }

    private void refreshSession(UserEntity user, HttpServletRequest request) {
        UserDetails userDetails = User.withUsername(user.getName())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()));
        SecurityContextHolder.setContext(context);
        request.getSession(true)
                .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean isPrivileged(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> PRIVILEGED_ROLES.contains(authority.getAuthority()));
    }

    private UserEntity findUserByAuthentication(Authentication authentication) {
        return userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private UserResponse toUserResponse(UserEntity user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return null;
        }

        String normalized = role.trim();
        if (normalized.isEmpty()) {
            return null;
        }

        String upper = normalized.toUpperCase(Locale.ROOT);
        if (!ALLOWED_USER_ROLES.contains(upper)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role");
        }

        return upper;
    }

    private ResponseEntity<ApiErrorResponse> notAuthenticated() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiErrorResponse("Not authenticated"));
    }

    private ResponseEntity<ApiErrorResponse> accessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiErrorResponse("Access denied"));
    }
}
