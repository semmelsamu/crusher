package de.othr.crusher.controller;

import de.othr.crusher.dto.api.ApiErrorResponse;
import de.othr.crusher.dto.api.statistics.StatisticConfigRequest;
import de.othr.crusher.dto.api.statistics.StatisticResponse;
import de.othr.crusher.model.GoEntity;
import de.othr.crusher.model.GoResult;
import de.othr.crusher.model.StatisticConfigEntity;
import de.othr.crusher.model.UserEntity;
import de.othr.crusher.repository.GoRepository;
import de.othr.crusher.repository.StatisticConfigRepository;
import de.othr.crusher.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the statistics API. Provides endpoints to configure and retrieve user
 * climbing statistics.
 */
@RestController
@RequestMapping("/api/statistics")
public class ApiStatisticsController {

  private final StatisticConfigRepository configRepository;
  private final GoRepository goRepository;
  private final UserRepository userRepository;

  public ApiStatisticsController(
      StatisticConfigRepository configRepository,
      GoRepository goRepository,
      UserRepository userRepository) {
    this.configRepository = configRepository;
    this.goRepository = goRepository;
    this.userRepository = userRepository;
  }

  /** Creates a new statistics configuration for the authenticated user. */
  @PostMapping
  @Transactional
  public ResponseEntity<?> createConfig(
      @RequestBody StatisticConfigRequest request, Authentication authentication) {
    UserEntity user = findUserByAuthentication(authentication);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    // Check if config already exists
    if (configRepository.findByUserId(user.getId()).isPresent()) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body(new ApiErrorResponse("Configuration already exists. Use PUT to update."));
    }

    StatisticConfigEntity config = new StatisticConfigEntity();
    config.setUser(user);
    config.setGoesPerGradeEnabled(request.goesPerGradeEnabled());
    config.setFinishedGoesPerGradeEnabled(request.finishedGoesPerGradeEnabled());
    config.setResultDistributionEnabled(request.resultDistributionEnabled());
    config.setHighestFinishedGradeEnabled(request.highestFinishedGradeEnabled());

    configRepository.save(config);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            Map.of(
                "message",
                "Configuration created successfully",
                "config",
                Map.of(
                    "goesPerGradeEnabled", config.isGoesPerGradeEnabled(),
                    "finishedGoesPerGradeEnabled", config.isFinishedGoesPerGradeEnabled(),
                    "resultDistributionEnabled", config.isResultDistributionEnabled(),
                    "highestFinishedGradeEnabled", config.isHighestFinishedGradeEnabled())));
  }

  /** Updates the statistics configuration for the authenticated user. */
  @PutMapping
  @Transactional
  public ResponseEntity<?> updateConfig(
      @RequestBody StatisticConfigRequest request, Authentication authentication) {
    UserEntity user = findUserByAuthentication(authentication);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    StatisticConfigEntity config = configRepository.findByUserId(user.getId()).orElse(null);

    if (config == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiErrorResponse("Configuration not found. Use POST to create."));
    }

    config.setGoesPerGradeEnabled(request.goesPerGradeEnabled());
    config.setFinishedGoesPerGradeEnabled(request.finishedGoesPerGradeEnabled());
    config.setResultDistributionEnabled(request.resultDistributionEnabled());
    config.setHighestFinishedGradeEnabled(request.highestFinishedGradeEnabled());

    configRepository.save(config);

    return ResponseEntity.ok(
        Map.of(
            "message",
            "Configuration updated successfully",
            "config",
            Map.of(
                "goesPerGradeEnabled", config.isGoesPerGradeEnabled(),
                "finishedGoesPerGradeEnabled", config.isFinishedGoesPerGradeEnabled(),
                "resultDistributionEnabled", config.isResultDistributionEnabled(),
                "highestFinishedGradeEnabled", config.isHighestFinishedGradeEnabled())));
  }

  /**
   * Retrieves statistics based on the user's configuration. Only returns data since the last fetch,
   * then updates the lastFetchedAt timestamp.
   */
  @GetMapping
  @Transactional
  public ResponseEntity<?> getStatistics(Authentication authentication) {
    UserEntity user = findUserByAuthentication(authentication);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiErrorResponse("Not authenticated"));
    }

    StatisticConfigEntity config = configRepository.findByUserId(user.getId()).orElse(null);

    if (config == null) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(new ApiErrorResponse("Configuration not found. Use POST to create one first."));
    }

    // Get goes since last fetch (or all goes if never fetched)
    List<GoEntity> goes;
    if (config.getLastFetchedAt() != null) {
      goes =
          goRepository.findBySession_UserIdAndTimestampAfter(
              user.getId(), config.getLastFetchedAt());
    } else {
      goes = goRepository.findBySession_UserId(user.getId());
    }

    // Calculate statistics based on enabled options
    Map<String, Long> goesPerGrade = null;
    Map<String, Long> finishedGoesPerGrade = null;
    Map<String, Long> resultDistribution = null;
    String highestFinishedGrade = null;

    if (config.isGoesPerGradeEnabled()) {
      goesPerGrade = calculateGoesPerGrade(goes);
    }

    if (config.isFinishedGoesPerGradeEnabled()) {
      finishedGoesPerGrade = calculateFinishedGoesPerGrade(goes);
    }

    if (config.isResultDistributionEnabled()) {
      resultDistribution = calculateResultDistribution(goes);
    }

    if (config.isHighestFinishedGradeEnabled()) {
      highestFinishedGrade = calculateHighestFinishedGrade(goes);
    }

    // Update lastFetchedAt timestamp
    config.setLastFetchedAt(LocalDateTime.now());
    configRepository.save(config);

    return ResponseEntity.ok(
        new StatisticResponse(
            goesPerGrade, finishedGoesPerGrade, resultDistribution, highestFinishedGrade));
  }

  /** Calculates the number of goes per grade. */
  private Map<String, Long> calculateGoesPerGrade(List<GoEntity> goes) {
    Map<String, Long> raw =
        goes.stream()
            .collect(
                Collectors.groupingBy(
                    go -> go.getBoulder().getGrade().getVScale(), Collectors.counting()));

    return sortByGrade(raw);
  }

  /** Calculates the number of finished goes per grade. */
  private Map<String, Long> calculateFinishedGoesPerGrade(List<GoEntity> goes) {
    Map<String, Long> raw =
        goes.stream()
            .filter(go -> go.getResult() == GoResult.FINISHED)
            .collect(
                Collectors.groupingBy(
                    go -> go.getBoulder().getGrade().getVScale(), Collectors.counting()));

    return sortByGrade(raw);
  }

  /** Calculates the distribution of go results. */
  private Map<String, Long> calculateResultDistribution(List<GoEntity> goes) {
    Map<String, Long> distribution = new LinkedHashMap<>();
    distribution.put(
        "FINISHED", goes.stream().filter(go -> go.getResult() == GoResult.FINISHED).count());
    distribution.put(
        "CLOSE_TRY", goes.stream().filter(go -> go.getResult() == GoResult.CLOSE_TRY).count());
    distribution.put(
        "DID_NOT_FINISH",
        goes.stream().filter(go -> go.getResult() == GoResult.DID_NOT_FINISH).count());
    return distribution;
  }

  /** Finds the highest grade among finished goes. */
  private String calculateHighestFinishedGrade(List<GoEntity> goes) {
    return goes.stream()
        .filter(go -> go.getResult() == GoResult.FINISHED)
        .map(go -> go.getBoulder().getGrade().getVScale())
        .max(Comparator.comparingInt(this::extractVScaleValue))
        .orElse(null);
  }

  /** Sorts a grade map by V-scale value. */
  private Map<String, Long> sortByGrade(Map<String, Long> map) {
    return map.entrySet().stream()
        .sorted(Comparator.comparingInt(e -> extractVScaleValue(e.getKey())))
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  /** Extracts the numeric value from a V-scale grade string. */
  private int extractVScaleValue(String grade) {
    if (grade == null || !grade.startsWith("V")) {
      return 0;
    }
    String numberPart = grade.substring(1).replace("+", "");
    try {
      return Integer.parseInt(numberPart);
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /** Finds the user from the authentication object. */
  private UserEntity findUserByAuthentication(Authentication authentication) {
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }
    return userRepository.findByName(authentication.getName()).orElse(null);
  }
}
