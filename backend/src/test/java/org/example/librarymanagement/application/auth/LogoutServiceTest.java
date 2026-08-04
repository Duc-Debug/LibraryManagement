package org.example.librarymanagement.application.auth;

import java.time.Instant;
import java.util.Set;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.token.AccessTokenVerificationResult;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("Thành công: token hợp lệ thì revoke và trả về success")
    void logout_WithValidToken_ShouldReturnSuccessResult() {
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
    @DisplayName("Thất bại: token null thì ném ValidationException")
    void logout_WithNullToken_ShouldThrowValidationException() {
        LogoutCommand command = new LogoutCommand(null);

        assertThrows(ValidationException.class, () -> logoutService.logout(command));

        verifyNoInteractions(accessTokenVerifierPort);
        verifyNoInteractions(accessTokenRevocationPort);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Thất bại: token rỗng hoặc chỉ chứa khoảng trắng")
    void logout_WithEmptyOrBlankToken_ShouldThrowValidationException(String invalidToken) {
        LogoutCommand command = new LogoutCommand(invalidToken);

        assertThrows(ValidationException.class, () -> logoutService.logout(command));

        verifyNoInteractions(accessTokenVerifierPort);
        verifyNoInteractions(accessTokenRevocationPort);
    }

    @Test
    @DisplayName("Thất bại: token không hợp lệ thì không revoke")
    void logout_WithRejectedToken_ShouldReturnFailureResult() {
        String token = "expired.jwt.token";
        LogoutCommand command = new LogoutCommand(token);

        when(accessTokenVerifierPort.verifyOrReject(token))
                .thenReturn(new AccessTokenVerificationResult.Rejected("Expired"));

        logoutService.logout(command);
        verify(accessTokenRevocationPort, never()).revoke(anyString(), any(Instant.class));
    }
}
