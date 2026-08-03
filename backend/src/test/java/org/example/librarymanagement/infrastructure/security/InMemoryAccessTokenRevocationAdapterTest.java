package org.example.librarymanagement.infrastructure.security;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InMemoryAccessTokenRevocationAdapterTest {

    @Test
    void revokesTokenUntilExpiration() {
        InMemoryAccessTokenRevocationAdapter adapter =
                new InMemoryAccessTokenRevocationAdapter();

        adapter.revoke(
                "token-id-123",
                Instant.now().plusSeconds(3600)
        );

        assertTrue(adapter.isRevoked("token-id-123"));
    }

    @Test
    void ignoresAlreadyExpiredToken() {
        InMemoryAccessTokenRevocationAdapter adapter =
                new InMemoryAccessTokenRevocationAdapter();

        adapter.revoke(
                "expired-token-id",
                Instant.now().minusSeconds(1)
        );

        assertFalse(adapter.isRevoked("expired-token-id"));
    }

    @Test
    void removesExpiredTokenDuringRevocationCheck() {
        InMemoryAccessTokenRevocationAdapter adapter =
                new InMemoryAccessTokenRevocationAdapter();

        adapter.revoke(
                "short-lived-token",
                Instant.now().plusMillis(20)
        );

        assertTrue(adapter.isRevoked("short-lived-token"));

        sleep(50);

        assertFalse(adapter.isRevoked("short-lived-token"));
    }

    @Test
    void scheduledCleanupRemovesExpiredTokens() {
        InMemoryAccessTokenRevocationAdapter adapter =
                new InMemoryAccessTokenRevocationAdapter();

        adapter.revoke(
                "token-to-clean",
                Instant.now().plusMillis(20)
        );

        assertTrue(adapter.isRevoked("token-to-clean"));

        sleep(50);

        adapter.removeExpiredTokens();

        assertFalse(adapter.isRevoked("token-to-clean"));
    }

    @Test
    void rejectsBlankTokenId() {
        InMemoryAccessTokenRevocationAdapter adapter =
                new InMemoryAccessTokenRevocationAdapter();

        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.revoke(" ", Instant.now().plusSeconds(3600))
        );
    }

    @Test
    void rejectsNullExpiration() {
        InMemoryAccessTokenRevocationAdapter adapter =
                new InMemoryAccessTokenRevocationAdapter();

        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.revoke("token-id", null)
        );
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new AssertionError("Test sleep was interrupted", exception);
        }
    }
}