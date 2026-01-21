package de.othr.crusher.dto;

import java.util.Map;

/**
 * Data Transfer Object for user climbing statistics.
 *
 * <p>Contains aggregated statistics about a user's climbing performance.
 */
public class UserStatistics {

  private final long totalBouldersFinished;
  private final long totalAttempts;
  private final Map<String, Long> finishedPerGrade;
  private final Map<String, Long> flashesPerGrade;
  private final String highestGrade;
  private final long didNotFinishCount;
  private final long closeTryCount;
  private final long finishedCount;

  public UserStatistics(
      long totalBouldersFinished,
      long totalAttempts,
      Map<String, Long> finishedPerGrade,
      Map<String, Long> flashesPerGrade,
      String highestGrade,
      long didNotFinishCount,
      long closeTryCount,
      long finishedCount) {
    this.totalBouldersFinished = totalBouldersFinished;
    this.totalAttempts = totalAttempts;
    this.finishedPerGrade = finishedPerGrade;
    this.flashesPerGrade = flashesPerGrade;
    this.highestGrade = highestGrade;
    this.didNotFinishCount = didNotFinishCount;
    this.closeTryCount = closeTryCount;
    this.finishedCount = finishedCount;
  }

  public long getTotalBouldersFinished() {
    return totalBouldersFinished;
  }

  public long getTotalAttempts() {
    return totalAttempts;
  }

  public Map<String, Long> getFinishedPerGrade() {
    return finishedPerGrade;
  }

  public Map<String, Long> getFlashesPerGrade() {
    return flashesPerGrade;
  }

  public String getHighestGrade() {
    return highestGrade;
  }

  public long getDidNotFinishCount() {
    return didNotFinishCount;
  }

  public long getCloseTryCount() {
    return closeTryCount;
  }

  public long getFinishedCount() {
    return finishedCount;
  }
}
