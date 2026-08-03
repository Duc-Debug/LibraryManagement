package org.example.librarymanagement.port.outbound.auth.token;

import java.time.Instant;

public record IssuedAccessToken(
        String tokenValue,
        String tokenId,
        Instant issuedAt,
        Instant expiresAt
) {

    public IssuedAccessToken {
        if (tokenValue == null || tokenValue.isBlank()) {
            throw new IllegalArgumentException(
                    "Token value must not be blank"
            );
        }

        if (tokenId == null || tokenId.isBlank()) {
            throw new IllegalArgumentException(
                    "Token ID must not be blank"
            );
        }

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

        if (!expiresAt.isAfter(issuedAt)) {
            throw new IllegalArgumentException(
                    "Expiration time must be after issued time"
            );
        }
    }
}