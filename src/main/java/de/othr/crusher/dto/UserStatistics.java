package de.othr.crusher.dto;

import java.util.Map;

/**
 * Data Transfer Object for user climbing statistics.
 * <p>
 * Contains aggregated statistics about a user's climbing performance.
 * </p>
 */
public class UserStatistics {

    private final long totalBouldersFinished;
    private final long totalAttempts;
    private final Map<String, Long> finishedPerGrade;
    private final Map<String, Long> flashesPerGrade;

    public UserStatistics(
            long totalBouldersFinished,
            long totalAttempts,
            Map<String, Long> finishedPerGrade,
            Map<String, Long> flashesPerGrade) {
        this.totalBouldersFinished = totalBouldersFinished;
        this.totalAttempts = totalAttempts;
        this.finishedPerGrade = finishedPerGrade;
        this.flashesPerGrade = flashesPerGrade;
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
}
