package org.example.librarymanagement.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.example.librarymanagement.port.outbound.auth.token.AccessTokenPayload;
import org.example.librarymanagement.port.outbound.auth.token.IssuedAccessToken;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtAccessTokenAdapterTest {

    private static final String SECRET =
            "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

    @Test
    void generatesTokenWithExpectedClaims() {
        JwtAccessTokenAdapter adapter = new JwtAccessTokenAdapter(
                new JwtProperties(SECRET, 3_600_000)
        );

        IssuedAccessToken issuedToken = adapter.issue(payload());
        String token = issuedToken.tokenValue();

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertNotNull(issuedToken.tokenId());
        assertNotNull(issuedToken.issuedAt());
        assertNotNull(issuedToken.expiresAt());

        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("alice", claims.getSubject());
        assertEquals(1, claims.get("userId", Integer.class));
        assertEquals(List.of("ADMIN"), claims.get("roles", List.class));
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
        assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
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
