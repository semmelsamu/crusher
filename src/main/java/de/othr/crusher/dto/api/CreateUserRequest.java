package de.othr.crusher.dto.api;

public record CreateUserRequest(String username, String email, String password, String role) {}
