package de.othr.crusher.dto.api;

public record UpdateUserRequest(String username, String email, String password, String role) {}
