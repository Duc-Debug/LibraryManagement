package org.example.librarymanagement.port.outbound.auth;

import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;

public interface AccessTokenVerifierPort {

    VerifiedAccessToken verify(String token);
}