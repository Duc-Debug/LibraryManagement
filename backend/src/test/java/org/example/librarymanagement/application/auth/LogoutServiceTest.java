package org.example.librarymanagement.application.auth;

import java.time.Instant;
import java.util.Set;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.token.AccessTokenVerificationResult;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private AccessTokenVerifierPort accessTokenVerifierPort;

    @Mock
    private AccessTokenRevocationPort accessTokenRevocationPort;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    void logoutRevokesValidToken() {
        String validToken = "valid.jwt.token.here";
        LogoutCommand command = new LogoutCommand(validToken);
        Instant expiresAt = Instant.now().plusSeconds(3600);
        VerifiedAccessToken verifiedToken = new VerifiedAccessToken(
                "token-id",
                1L,
                "alice",
                Set.of("USER"),
                Instant.now(),
                expiresAt
        );

        when(accessTokenVerifierPort.verifyOrReject(validToken))
                .thenReturn(new AccessTokenVerificationResult.Valid(verifiedToken));

        logoutService.logout(command);

        verify(accessTokenRevocationPort).revoke(
                verifiedToken.tokenId(),
                verifiedToken.expiresAt()
        );
    }

    @Test
    void logoutRejectsNullToken() {
        LogoutCommand command = new LogoutCommand(null);

        assertThrows(ValidationException.class, () -> logoutService.logout(command));

        verifyNoInteractions(accessTokenVerifierPort);
        verifyNoInteractions(accessTokenRevocationPort);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void logoutRejectsBlankToken(String invalidToken) {
        LogoutCommand command = new LogoutCommand(invalidToken);

        assertThrows(ValidationException.class, () -> logoutService.logout(command));

        verifyNoInteractions(accessTokenVerifierPort);
        verifyNoInteractions(accessTokenRevocationPort);
    }

    @Test
    void logoutDoesNotRevokeRejectedToken() {
        String token = "expired.jwt.token";
        LogoutCommand command = new LogoutCommand(token);

        when(accessTokenVerifierPort.verifyOrReject(token))
                .thenReturn(new AccessTokenVerificationResult.Rejected("Expired"));

        logoutService.logout(command);

        verify(accessTokenRevocationPort, never()).revoke(anyString(), any(Instant.class));
    }
}
