package de.othr.crusher.dto.api.statistics;

import java.util.Map;

/**
 * Response DTO for statistics API.
 * Fields are null if not enabled in the user's configuration.
 */
public record StatisticResponse(
    Map<String, Long> goesPerGrade,
    Map<String, Long> finishedGoesPerGrade,
    Map<String, Long> resultDistribution,
    String highestFinishedGrade
) {}
