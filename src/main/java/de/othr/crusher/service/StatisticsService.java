package de.othr.crusher.service;

import de.othr.crusher.dto.UserStatistics;
import de.othr.crusher.model.GoEntity;
import de.othr.crusher.model.GoResult;
import de.othr.crusher.repository.GoRepository;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service for calculating user climbing statistics. */
@Service
public class StatisticsService {

  private final GoRepository goRepository;

  public StatisticsService(GoRepository goRepository) {
    this.goRepository = goRepository;
  }

  /**
   * Calculates comprehensive statistics for a given user.
   *
   * @param userId the user's identifier
   * @return aggregated user statistics
   */
  @Transactional(readOnly = true)
  public UserStatistics getUserStatistics(Long userId) {
    List<GoEntity> allGoes = goRepository.findBySession_UserId(userId);

    long totalAttempts = allGoes.size();

    // Group goes by boulder to find finished boulders and flashes
    Map<Long, List<GoEntity>> goesByBoulder =
        allGoes.stream().collect(Collectors.groupingBy(go -> go.getBoulder().getId()));

    // Find unique boulders that were finished
    long totalBouldersFinished =
        goesByBoulder.entrySet().stream()
            .filter(
                entry ->
                    entry.getValue().stream().anyMatch(go -> go.getResult() == GoResult.FINISHED))
            .count();

    // Find the highest grade in all goes
    int maxGrade =
        allGoes.stream()
            .map(go -> extractVScaleValue(go.getBoulder().getGrade().getVScale()))
            .max(Integer::compare)
            .orElse(0);

    // Count finished boulders per grade
    Map<String, Long> finishedPerGradeRaw =
        goesByBoulder.entrySet().stream()
            .filter(
                entry ->
                    entry.getValue().stream().anyMatch(go -> go.getResult() == GoResult.FINISHED))
            .map(entry -> entry.getValue().get(0).getBoulder())
            .collect(
                Collectors.groupingBy(
                    boulder -> boulder.getGrade().getVScale(), Collectors.counting()));

    // Fill in missing grades from V0 to maxGrade
    Map<String, Long> finishedPerGrade = new LinkedHashMap<>();
    for (int i = 0; i <= maxGrade; i++) {
      String grade = "V" + i;
      finishedPerGrade.put(grade, finishedPerGradeRaw.getOrDefault(grade, 0L));
    }

    // Count flashes per grade (first attempt was FINISHED)
    Map<String, Long> flashesPerGradeRaw =
        goesByBoulder.entrySet().stream()
            .filter(
                entry -> {
                  List<GoEntity> goes = entry.getValue();
                  GoEntity firstGo =
                      goes.stream().min(Comparator.comparing(GoEntity::getTimestamp)).orElse(null);
                  return firstGo != null && firstGo.getResult() == GoResult.FINISHED;
                })
            .map(entry -> entry.getValue().get(0).getBoulder())
            .collect(
                Collectors.groupingBy(
                    boulder -> boulder.getGrade().getVScale(), Collectors.counting()));

    // Fill in missing grades from V0 to maxGrade
    Map<String, Long> flashesPerGrade = new LinkedHashMap<>();
    for (int i = 0; i <= maxGrade; i++) {
      String grade = "V" + i;
      flashesPerGrade.put(grade, flashesPerGradeRaw.getOrDefault(grade, 0L));
    }

    // Find highest grade from finished boulders
    String highestGrade =
        allGoes.stream()
            .filter(go -> go.getResult() == GoResult.FINISHED)
            .map(go -> go.getBoulder().getGrade().getVScale())
            .max(Comparator.comparingInt(this::extractVScaleValue))
            .orElse(null);

    // Count goes by result type
    long didNotFinishCount =
        allGoes.stream().filter(go -> go.getResult() == GoResult.DID_NOT_FINISH).count();
    long closeTryCount =
        allGoes.stream().filter(go -> go.getResult() == GoResult.CLOSE_TRY).count();
    long finishedCount = allGoes.stream().filter(go -> go.getResult() == GoResult.FINISHED).count();

    return new UserStatistics(
        totalBouldersFinished,
        totalAttempts,
        finishedPerGrade,
        flashesPerGrade,
        highestGrade,
        didNotFinishCount,
        closeTryCount,
        finishedCount);
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
}
