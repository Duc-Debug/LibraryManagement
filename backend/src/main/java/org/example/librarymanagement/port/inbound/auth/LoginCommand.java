package org.example.librarymanagement.port.inbound.auth;

public record LoginCommand(
        String username,
        String password
) {
}
