package org.example.librarymanagement.application.auth;

import java.util.Objects;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.token.AccessTokenVerificationResult;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;

public class LogoutService implements LogoutUseCase {

    private final AccessTokenVerifierPort accessTokenVerifierPort;
    private final AccessTokenRevocationPort accessTokenRevocationPort;

   public LogoutService(
        AccessTokenVerifierPort accessTokenVerifierPort,
        AccessTokenRevocationPort accessTokenRevocationPort
) {
    this.accessTokenVerifierPort = Objects.requireNonNull(
            accessTokenVerifierPort,
            "Access token verifier port must not be null"
    );

    this.accessTokenRevocationPort = Objects.requireNonNull(
            accessTokenRevocationPort,
            "Access token revocation port must not be null"
    );
}

    @Override
    public void logout(LogoutCommand command) {
        if (command == null) {
            throw new ValidationException(
                    "Logout command cannot be null."
            );
        }

        String token = command.token();

        if (token == null || token.isBlank()) {
            throw new ValidationException(
                    "Token cannot be null or empty."
            );
        }

        AccessTokenVerificationResult verificationResult =
                accessTokenVerifierPort.verifyOrReject(token);

        if (verificationResult instanceof AccessTokenVerificationResult.Valid valid) {
            VerifiedAccessToken verifiedToken = valid.token();

            accessTokenRevocationPort.revoke(
                    verifiedToken.tokenId(),
                    verifiedToken.expiresAt()
            );
        }
    }
}