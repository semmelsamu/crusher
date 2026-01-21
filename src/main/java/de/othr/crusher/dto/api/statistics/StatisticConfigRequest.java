package de.othr.crusher.dto.api.statistics;

/**
 * Request DTO for creating or updating a statistics configuration.
 */
public record StatisticConfigRequest(
    boolean goesPerGradeEnabled,
    boolean finishedGoesPerGradeEnabled,
    boolean resultDistributionEnabled,
    boolean highestFinishedGradeEnabled
) {}
