package org.example.librarymanagement.infrastructure.security;

import java.util.List;
import java.util.Set;

import org.example.librarymanagement.port.outbound.auth.token.AccessTokenPayload;
import org.example.librarymanagement.port.outbound.auth.token.IssuedAccessToken;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
class JwtTokenProviderAdapterTest {

    private static final String SECRET =
            "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

    @Test
    void issuesTokenWithExpectedClaims() {
        JwtAccessTokenAdapter adapter = new JwtAccessTokenAdapter(
                new JwtProperties(SECRET, 3_600_000)
        );

        IssuedAccessToken token = adapter.issue(payload());

        assertNotNull(token.tokenValue());
        assertFalse(token.tokenValue().isBlank());
        assertNotNull(token.tokenId());
        assertNotNull(token.issuedAt());
        assertNotNull(token.expiresAt());
        assertTrue(token.expiresAt().isAfter(token.issuedAt()));

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .build()
                .parseSignedClaims(token.tokenValue())
                .getPayload();

        assertEquals(token.tokenId(), claims.getId());
        assertEquals("alice", claims.getSubject());
        assertEquals(1L, ((Number) claims.get("userId")).longValue());
        assertEquals(List.of("ADMIN"), claims.get("roles", List.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
    }

    @Test
    void verifiesIssuedToken() {
        JwtAccessTokenAdapter adapter = new JwtAccessTokenAdapter(
                new JwtProperties(SECRET, 3_600_000)
        );
        IssuedAccessToken issuedToken = adapter.issue(payload());

        VerifiedAccessToken verifiedToken = adapter.verify(
                issuedToken.tokenValue()
        );

        assertEquals(issuedToken.tokenId(), verifiedToken.tokenId());
        assertEquals(1L, verifiedToken.userId());
        assertEquals("alice", verifiedToken.username());
        assertEquals(Set.of("ADMIN"), verifiedToken.roles());
        assertEquals(
                issuedToken.issuedAt().getEpochSecond(),
                verifiedToken.issuedAt().getEpochSecond()
        );
        assertEquals(
                issuedToken.expiresAt().getEpochSecond(),
                verifiedToken.expiresAt().getEpochSecond()
        );
    }

    @Test
    void rejectsBlankSecret() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtAccessTokenAdapter(new JwtProperties(" ", 1000))
        );
    }

    @Test
    void rejectsInvalidBase64Secret() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtAccessTokenAdapter(
                        new JwtProperties("not base64", 1000)
                )
        );
    }

    @Test
    void rejectsNonPositiveExpiration() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtAccessTokenAdapter(new JwtProperties(SECRET, 0))
        );
    }

    @Test
    void rejectsNullPayload() {
        JwtAccessTokenAdapter adapter = new JwtAccessTokenAdapter(
                new JwtProperties(SECRET, 1000)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.issue(null)
        );
    }

    private AccessTokenPayload payload() {
        return new AccessTokenPayload(
                1L,
                "alice",
                Set.of("ADMIN")
        );
    }
}
