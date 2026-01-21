package de.othr.crusher.dto.api;

/** Request DTO for updating an existing climbing attempt (go). */
public record UpdateGoRequest(String result, Integer progressedHold) {}
