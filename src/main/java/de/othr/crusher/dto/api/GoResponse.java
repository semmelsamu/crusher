package de.othr.crusher.dto.api;

import java.time.LocalDateTime;

/** Response DTO for a climbing attempt (go). */
public record GoResponse(
    Long id,
    Long sessionId,
    Long boulderId,
    String result,
    LocalDateTime timestamp,
    Integer progressedHold) {}
