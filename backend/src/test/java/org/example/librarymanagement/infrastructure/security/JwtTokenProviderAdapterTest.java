package org.example.librarymanagement.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderAdapterTest {

    private static final String SECRET =
            "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

    @Test
    void generatesTokenWithExpectedClaims() {
        JwtTokenProviderAdapter adapter = new JwtTokenProviderAdapter(
                new JwtProperties(SECRET, 3_600_000)
        );

        String token = adapter.generateAccessToken(user());

        assertNotNull(token);
        assertFalse(token.isBlank());

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
                () -> new JwtTokenProviderAdapter(new JwtProperties(" ", 1000))
        );
    }

    @Test
    void rejectsInvalidBase64Secret() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtTokenProviderAdapter(
                        new JwtProperties("not base64", 1000)
                )
        );
    }

    @Test
    void rejectsNonPositiveExpiration() {
        assertThrows(
                IllegalStateException.class,
                () -> new JwtTokenProviderAdapter(new JwtProperties(SECRET, 0))
        );
    }

    @Test
    void rejectsNullUser() {
        JwtTokenProviderAdapter adapter = new JwtTokenProviderAdapter(
                new JwtProperties(SECRET, 1000)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.generateAccessToken(null)
        );
    }

    private User user() {
        LocalDateTime now = LocalDateTime.now();
        return new User(
                1L,
                "alice",
                "$2a$10$passwordHash",
                "Alice Reader",
                "alice@example.test",
                "0123456789",
                true,
                null,
                now,
                now,
                Set.of(new Role(1L, "admin", "Admin"))
        );
    }
}
