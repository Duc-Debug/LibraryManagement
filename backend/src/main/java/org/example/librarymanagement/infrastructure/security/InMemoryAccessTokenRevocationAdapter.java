package org.example.librarymanagement.infrastructure.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
/*
 * In-memory revocation storage for local/demo usage.
 * For production, replace this adapter with Redis or database storage
 * that supports TTL/cleanup across application instances.
 */
@Component
public class InMemoryAccessTokenRevocationAdapter
        implements AccessTokenRevocationPort {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    @Override
    public void revoke(String tokenId, Instant expiresAt) {
        if (tokenId == null || tokenId.isBlank()) {
            throw new IllegalArgumentException("Token ID must not be blank");
        }

        if (expiresAt == null) {
            throw new IllegalArgumentException("Token expiration must not be null");
        }

        if (expiresAt.isAfter(Instant.now())) {
            revokedTokens.put(tokenId, expiresAt);
        }
    }

    @Override
    public boolean isRevoked(String tokenId) {
        if (tokenId == null || tokenId.isBlank()) {
            return false;
        }

        Instant expiresAt = revokedTokens.get(tokenId);

        if (expiresAt == null) {
            return false;
        }

        if (!expiresAt.isAfter(Instant.now())) {
            revokedTokens.remove(tokenId);
            return false;
        }

        return true;
    }
   @Scheduled(fixedDelay = 60000)
void removeExpiredTokens() {
    Instant now = Instant.now();

    revokedTokens.entrySet().removeIf(entry ->
            !entry.getValue().isAfter(now)
    );

}
}