package org.example.librarymanagement.port.outbound.auth;

import org.example.librarymanagement.port.outbound.auth.token.AccessTokenPayload;
import org.example.librarymanagement.port.outbound.auth.token.IssuedAccessToken;
public interface AccessTokenIssuerPort {

    IssuedAccessToken issue(
            AccessTokenPayload payload
    );
}