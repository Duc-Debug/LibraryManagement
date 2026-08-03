package org.example.librarymanagement.infrastructure.web.auth;

public record LogoutResponse(
    boolean success,
    String message
) {}