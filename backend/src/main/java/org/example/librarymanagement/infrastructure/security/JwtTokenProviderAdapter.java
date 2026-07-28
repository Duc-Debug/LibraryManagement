package org.example.librarymanagement.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.auth.TokenProviderPort;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProviderAdapter implements TokenProviderPort {

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;

    public JwtTokenProviderAdapter(JwtProperties jwtProperties) {
        validateProperties(jwtProperties);

        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "JWT secret must be valid Base64 and strong enough for HMAC signing",
                    exception
            );
        }

        this.accessTokenExpirationMs = jwtProperties.accessTokenExpirationMs();
    }

    @Override
    public String generateAccessToken(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        Instant issuedAt = Instant.now();
        Instant expiration = issuedAt.plusMillis(accessTokenExpirationMs);

        List<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("roles", roleNames)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    private void validateProperties(JwtProperties jwtProperties) {
        if (jwtProperties == null) {
            throw new IllegalArgumentException("JWT properties must not be null");
        }

        if (jwtProperties.secret() == null || jwtProperties.secret().isBlank()) {
            throw new IllegalStateException("JWT secret must be configured");
        }

        if (jwtProperties.accessTokenExpirationMs() <= 0) {
            throw new IllegalStateException("JWT expiration must be greater than zero");
        }
    }
}
