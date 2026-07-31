package org.example.librarymanagement.port.outbound.auth.token;

import java.util.Set;

public record AccessTokenPayload(
        Long userId,
        String username,
        Set<String> roles
) {

    public AccessTokenPayload {
        if (userId == null) {
            throw new IllegalArgumentException(
                    "User ID must not be null"
            );
        }

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username must not be blank"
            );
        }

        username = username.trim();

        roles = roles == null
                ? Set.of()
                : Set.copyOf(roles);
    }
}