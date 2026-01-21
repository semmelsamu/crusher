package de.othr.crusher.controller;

import de.othr.crusher.dto.api.ApiErrorResponse;
import de.othr.crusher.dto.api.CreateGoRequest;
import de.othr.crusher.dto.api.GoResponse;
import de.othr.crusher.dto.api.UpdateGoRequest;
import de.othr.crusher.model.BoulderEntity;
import de.othr.crusher.model.GoEntity;
import de.othr.crusher.model.GoResult;
import de.othr.crusher.model.SessionEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.BoulderRepository;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.ProjectRepository;
import de.othr.crusher.repository.SessionRepository;
import de.othr.crusher.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

/**
 * REST API controller for managing climbing attempts (goes) within a session.
 *
 * <p>Provides CRUD operations for tracking boulder attempts during a climbing session. All
 * operations are nested under the session resource path at /api/sessions/{sessionId}/goes.
 */
@RestController
@RequestMapping("/api/sessions/{sessionId}/goes")
public class ApiGoController {

  private final GoRepository goRepository;
  private final SessionRepository sessionRepository;
  private final BoulderRepository boulderRepository;
  private final UserRepository userRepository;
  private final ProjectRepository projectRepository;

  public ApiGoController(
      GoRepository goRepository,
      SessionRepository sessionRepository,
      BoulderRepository boulderRepository,
      UserRepository userRepository,
      ProjectRepository projectRepository) {
    this.goRepository = goRepository;
    this.sessionRepository = sessionRepository;
    this.boulderRepository = boulderRepository;
    this.userRepository = userRepository;
    this.projectRepository = projectRepository;
  }

  /**
   * Lists all goes for a given session.
   *
   * @param sessionId identifier of the parent session
   * @param authentication the authenticated user
   * @return list of goes or error response
   */
  @GetMapping
  @Transactional(readOnly = true)
  public ResponseEntity<?> listGoes(
      @PathVariable("sessionId") Long sessionId, Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    UserEntity user = findUserByAuthentication(authentication);
    validateAndGetSession(sessionId, user);

    List<GoEntity> goes = goRepository.findBySessionIdOrderByTimestampDesc(sessionId);
    List<GoResponse> response = goes.stream().map(this::toGoResponse).collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  /**
   * Gets a specific go by ID.
   *
   * @param sessionId identifier of the parent session
   * @param goId identifier of the go
   * @param authentication the authenticated user
   * @return go details or error response
   */
  @GetMapping("/{goId}")
  @Transactional(readOnly = true)
  public ResponseEntity<?> getGo(
      @PathVariable("sessionId") Long sessionId,
      @PathVariable("goId") Long goId,
      Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    UserEntity user = findUserByAuthentication(authentication);
    validateAndGetSession(sessionId, user);
    GoEntity go = findGoInSessionOrThrow(sessionId, goId);

    return ResponseEntity.ok(toGoResponse(go));
  }

  /**
   * Creates a new go for the given session.
   *
   * @param sessionId identifier of the parent session
   * @param request go creation request
   * @param authentication the authenticated user
   * @return created go or error response
   */
  @PostMapping
  @Transactional
  public ResponseEntity<?> createGo(
      @PathVariable("sessionId") Long sessionId,
      @RequestBody(required = false) CreateGoRequest request,
      Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    if (request == null) {
      return ResponseEntity.badRequest().body(new ApiErrorResponse("Request body is required"));
    }

    // Validate required fields
    if (request.boulderId() == null) {
      return ResponseEntity.badRequest().body(new ApiErrorResponse("Boulder is required"));
    }

    if (request.result() == null || request.result().isBlank()) {
      return ResponseEntity.badRequest().body(new ApiErrorResponse("Result is required"));
    }

    // Validate result enum
    GoResult result;
    try {
      result = GoResult.valueOf(request.result());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(
              new ApiErrorResponse(
                  "Invalid result value. Must be one of: DID_NOT_FINISH, CLOSE_TRY, FINISHED"));
    }

    UserEntity user = findUserByAuthentication(authentication);
    SessionEntity session = validateAndGetSession(sessionId, user);

    // Validate boulder exists and is not deleted
    BoulderEntity boulder =
        boulderRepository
            .findByIdAndDeletedFalse(request.boulderId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid boulder"));

    // Create go entity
    GoEntity go = new GoEntity();
    go.setSession(session);
    go.setBoulder(boulder);
    go.setResult(result);
    go.setTimestamp(request.timestamp() != null ? request.timestamp() : LocalDateTime.now());
    go.setProgressedHold(request.progressedHold());

    // Validate progressed hold
    String validationError = validateProgressedHold(go, boulder);
    if (validationError != null) {
      return ResponseEntity.badRequest().body(new ApiErrorResponse(validationError));
    }

    goRepository.save(go);

    // Auto-remove project when boulder is finished
    if (go.getResult() == GoResult.FINISHED) {
      projectRepository
          .findByUserIdAndBoulderId(user.getId(), boulder.getId())
          .ifPresent(projectRepository::delete);
    }

    return ResponseEntity.status(HttpStatus.CREATED).body(toGoResponse(go));
  }

  /**
   * Updates an existing go.
   *
   * @param sessionId identifier of the parent session
   * @param goId identifier of the go
   * @param request go update request
   * @param authentication the authenticated user
   * @return updated go or error response
   */
  @PutMapping("/{goId}")
  @Transactional
  public ResponseEntity<?> updateGo(
      @PathVariable("sessionId") Long sessionId,
      @PathVariable("goId") Long goId,
      @RequestBody(required = false) UpdateGoRequest request,
      Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    if (request == null) {
      return ResponseEntity.badRequest().body(new ApiErrorResponse("Request body is required"));
    }

    if (request.result() == null || request.result().isBlank()) {
      return ResponseEntity.badRequest().body(new ApiErrorResponse("Result is required"));
    }

    // Validate result enum
    GoResult result;
    try {
      result = GoResult.valueOf(request.result());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest()
          .body(
              new ApiErrorResponse(
                  "Invalid result value. Must be one of: DID_NOT_FINISH, CLOSE_TRY, FINISHED"));
    }

    UserEntity user = findUserByAuthentication(authentication);
    validateAndGetSession(sessionId, user);
    GoEntity go = findGoInSessionOrThrow(sessionId, goId);

    // Update fields
    go.setResult(result);
    go.setProgressedHold(request.progressedHold());

    // Validate progressed hold
    String validationError = validateProgressedHold(go, go.getBoulder());
    if (validationError != null) {
      return ResponseEntity.badRequest().body(new ApiErrorResponse(validationError));
    }

    goRepository.save(go);

    // Auto-remove project when go is updated to finished
    if (go.getResult() == GoResult.FINISHED) {
      projectRepository
          .findByUserIdAndBoulderId(user.getId(), go.getBoulder().getId())
          .ifPresent(projectRepository::delete);
    }

    return ResponseEntity.ok(toGoResponse(go));
  }

  /**
   * Deletes an existing go.
   *
   * @param sessionId identifier of the parent session
   * @param goId identifier of the go
   * @param authentication the authenticated user
   * @return no content or error response
   */
  @DeleteMapping("/{goId}")
  @Transactional
  public ResponseEntity<?> deleteGo(
      @PathVariable("sessionId") Long sessionId,
      @PathVariable("goId") Long goId,
      Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    UserEntity user = findUserByAuthentication(authentication);
    validateAndGetSession(sessionId, user);
    GoEntity go = findGoInSessionOrThrow(sessionId, goId);

    goRepository.delete(go);

    return ResponseEntity.noContent().build();
  }

  /**
   * Converts a GoEntity to a GoResponse DTO.
   *
   * @param go the go entity
   * @return go response DTO
   */
  private GoResponse toGoResponse(GoEntity go) {
    return new GoResponse(
        go.getId(),
        go.getSession().getId(),
        go.getBoulder().getId(),
        go.getResult().name(),
        go.getTimestamp(),
        go.getProgressedHold());
  }

  /**
   * Finds the current user from the security authentication.
   *
   * @param authentication the authenticated user
   * @return the UserEntity for the current user
   * @throws ResponseStatusException if the user is not found
   */
  private UserEntity findUserByAuthentication(Authentication authentication) {
    return userRepository
        .findByName(authentication.getName())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
  }

  /**
   * Validates that a session exists and belongs to the current user.
   *
   * @param sessionId the session identifier
   * @param user the current user
   * @return the SessionEntity
   * @throws ResponseStatusException if the session is not found or doesn't belong to the user
   */
  private SessionEntity validateAndGetSession(Long sessionId, UserEntity user) {
    SessionEntity session =
        sessionRepository
            .findById(sessionId)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));

    if (!session.getUser().getId().equals(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    return session;
  }

  /**
   * Finds a go and verifies it belongs to the specified session.
   *
   * @param sessionId the session identifier
   * @param goId the go identifier
   * @return the GoEntity
   * @throws ResponseStatusException if the go is not found or doesn't belong to the session
   */
  private GoEntity findGoInSessionOrThrow(Long sessionId, Long goId) {
    GoEntity go =
        goRepository
            .findById(goId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Go not found"));

    if (go.getSession() == null || !go.getSession().getId().equals(sessionId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Go does not belong to session");
    }

    return go;
  }

  /**
   * Validates the progressed hold value against the boulder's hold count.
   *
   * @param go the go entity with progressed hold to validate
   * @param boulder the boulder entity
   * @return error message if validation fails, null otherwise
   */
  private String validateProgressedHold(GoEntity go, BoulderEntity boulder) {
    Integer progressedHold = go.getProgressedHold();

    // Progressed hold is optional
    if (progressedHold == null) {
      return null;
    }

    if (progressedHold < 0) {
      return "Progressed hold cannot be negative";
    }

    Integer holdsCount = boulder.getHoldsCount();
    if (holdsCount != null && progressedHold > holdsCount) {
      return "Progressed hold cannot exceed the boulder's total holds (" + holdsCount + ")";
    }

    return null;
  }
}
