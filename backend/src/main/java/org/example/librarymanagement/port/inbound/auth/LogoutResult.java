package org.example.librarymanagement.port.inbound.auth;

public record LogoutResult(
        boolean success,
        String message
) {
}
