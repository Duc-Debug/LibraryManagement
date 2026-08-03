package org.example.librarymanagement.port.outbound.auth.token;

public sealed interface AccessTokenVerificationResult
        permits AccessTokenVerificationResult.Valid,
                AccessTokenVerificationResult.Rejected {

    record Valid(VerifiedAccessToken token)
            implements AccessTokenVerificationResult {
    }

    record Rejected(String reason)
            implements AccessTokenVerificationResult {
    }
}