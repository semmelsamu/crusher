package de.othr.crusher.dto.api;

import java.time.LocalDateTime;

/** Request DTO for creating a new climbing attempt (go). */
public record CreateGoRequest(
    Long boulderId, String result, LocalDateTime timestamp, Integer progressedHold) {}
