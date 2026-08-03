package org.example.librarymanagement.application.auth;

import java.time.Instant;
import java.util.Set;

import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.token.AccessTokenVerificationResult;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LogoutServiceTest {

    private AccessTokenVerifierPort accessTokenVerifierPort;
    private AccessTokenRevocationPort accessTokenRevocationPort;
    private LogoutService logoutService;

    @BeforeEach
    void setUp() {
        accessTokenVerifierPort = mock(AccessTokenVerifierPort.class);
        accessTokenRevocationPort = mock(AccessTokenRevocationPort.class);

        logoutService = new LogoutService(
                accessTokenVerifierPort,
                accessTokenRevocationPort
        );
    }

    @Test
    void rejectsNullCommand() {
        assertThrows(
                RuntimeException.class,
                () -> logoutService.logout(null)
        );
    }

    @Test
    void rejectsBlankToken() {
        assertThrows(
                RuntimeException.class,
                () -> logoutService.logout(new LogoutCommand(" "))
        );
    }

    @Test
    void revokesValidTokenByTokenIdUntilExpiration() {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusSeconds(3600);

        VerifiedAccessToken verifiedToken = new VerifiedAccessToken(
                "token-id-123",
                1L,
                "alice",
                Set.of("ADMIN"),
                issuedAt,
                expiresAt
        );

        when(accessTokenVerifierPort.verifyOrReject("raw-jwt"))
                .thenReturn(new AccessTokenVerificationResult.Valid(
                        verifiedToken
                ));

        logoutService.logout(new LogoutCommand("raw-jwt"));

        verify(accessTokenVerifierPort).verifyOrReject("raw-jwt");
        verify(accessTokenRevocationPort).revoke(
                "token-id-123",
                expiresAt
        );
    }

    @Test
    void treatsRejectedTokenAsSuccessfulLogoutNoOp() {
        when(accessTokenVerifierPort.verifyOrReject("invalid-token"))
                .thenReturn(new AccessTokenVerificationResult.Rejected(
                        "Access token is invalid"
                ));

        assertDoesNotThrow(
                () -> logoutService.logout(new LogoutCommand("invalid-token"))
        );

        verify(accessTokenVerifierPort).verifyOrReject("invalid-token");
        verify(accessTokenRevocationPort, never()).revoke(any(), any());
    }
}