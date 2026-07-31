package org.example.librarymanagement.port.outbound.auth;
import java.time.Instant;

public interface AccessTokenRevocationPort {

    void revoke(String tokenId, Instant expiresAt);

    boolean isRevoked(String tokenId);
}