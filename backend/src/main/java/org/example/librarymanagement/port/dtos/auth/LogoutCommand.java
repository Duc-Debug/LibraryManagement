package org.example.librarymanagement.port.dtos.auth;

public record LogoutCommand(
        String token
) {
}