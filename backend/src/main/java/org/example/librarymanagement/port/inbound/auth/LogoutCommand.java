package org.example.librarymanagement.port.inbound.auth;

public record LogoutCommand(
        String token
) {
}