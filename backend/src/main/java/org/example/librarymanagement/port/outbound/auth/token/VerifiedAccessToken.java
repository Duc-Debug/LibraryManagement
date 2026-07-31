package org.example.librarymanagement.port.outbound.auth.token;

import java.time.Instant;
import java.util.Set;

public record VerifiedAccessToken(
        String tokenId,
        Long userId,
        String username,
        Set<String> roles,
        Instant issuedAt,
        Instant expiresAt
) {

    public VerifiedAccessToken {
        if (tokenId == null || tokenId.isBlank()) {
            throw new IllegalArgumentException(
                    "Token ID must not be blank"
            );
        }

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

        if (issuedAt == null) {
            throw new IllegalArgumentException(
                    "Issued time must not be null"
            );
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException(
                    "Expiration time must not be null"
            );
        }
    }
}