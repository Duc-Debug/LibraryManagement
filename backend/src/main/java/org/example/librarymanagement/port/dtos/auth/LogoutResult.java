package org.example.librarymanagement.port.dtos.auth;

public record LogoutResult(
        boolean success,
        String message
) {
}
