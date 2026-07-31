package org.example.librarymanagement.port.dtos.auth;

public record LoginCommand(
        String username,
        String password
) {
}
