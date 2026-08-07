package org.example.librarymanagement.infrastructure.security;

import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.example.librarymanagement.port.outbound.auth.AccessTokenIssuerPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.token.AccessTokenPayload;
import org.example.librarymanagement.port.outbound.auth.token.AccessTokenVerificationResult;
import org.example.librarymanagement.port.outbound.auth.token.ExpiredAccessTokenException;
import org.example.librarymanagement.port.outbound.auth.token.InvalidAccessTokenException;
import org.example.librarymanagement.port.outbound.auth.token.IssuedAccessToken;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Component

public class JwtAccessTokenAdapter
        implements AccessTokenIssuerPort,
                   AccessTokenVerifierPort {

    private static final String USER_ID_CLAIM = "userId";
    private static final String ROLES_CLAIM = "roles";

    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;

    public JwtAccessTokenAdapter(
            JwtProperties jwtProperties
    ) {
        validateProperties(jwtProperties);

        try {
            byte[] keyBytes = Decoders.BASE64.decode(
                    jwtProperties.secret()
            );

            this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        } catch (RuntimeException exception) {
            throw new IllegalStateException(
                    "JWT secret must be valid Base64 "
                            + "and strong enough for HMAC signing",
                    exception
            );
        }

        this.accessTokenExpirationMs =
                jwtProperties.accessTokenExpirationMs();
    }

    @Override
    public IssuedAccessToken issue(
            AccessTokenPayload payload
    ) {
        if (payload == null) {
            throw new IllegalArgumentException(
                    "Access token payload must not be null"
            );
        }

        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plusMillis(
                accessTokenExpirationMs
        );

        String tokenId = UUID.randomUUID().toString();

        String tokenValue = Jwts.builder()
                .id(tokenId)
                .subject(payload.username())
                .claim(USER_ID_CLAIM, payload.userId())
                .claim(ROLES_CLAIM, payload.roles())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .signWith(signingKey)
                .compact();

        return new IssuedAccessToken(
                tokenValue,
                tokenId,
                issuedAt,
                expiresAt
        );
    }
    @Override
    public AccessTokenVerificationResult verifyOrReject(String token) {
    try {
        return new AccessTokenVerificationResult.Valid(
                verify(token)
        );
    } catch (InvalidAccessTokenException exception) {
        return new AccessTokenVerificationResult.Rejected(
                exception.getMessage()
        );
    }
}

    @Override
    public VerifiedAccessToken verify(String token) {
        validateRawToken(token);

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return mapToVerifiedAccessToken(claims);
        } catch (ExpiredJwtException exception) {
            throw new ExpiredAccessTokenException(
                    "Access token has expired",
                    exception
            );
        } catch (JwtException | IllegalArgumentException exception) {
            throw new InvalidAccessTokenException(
                    "Access token is invalid",
                    exception
            );
        }
    }

    private VerifiedAccessToken mapToVerifiedAccessToken(
            Claims claims
    ) {
        String tokenId = claims.getId();
        String username = claims.getSubject();

        Long userId = extractUserId(claims);
        Set<String> roles = extractRoles(claims);

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        validateRequiredClaims(
                tokenId,
                userId,
                username,
                issuedAt,
                expiration
        );

        return new VerifiedAccessToken(
                tokenId,
                userId,
                username,
                roles,
                issuedAt.toInstant(),
                expiration.toInstant()
        );
    }

    private Long extractUserId(Claims claims) {
        Object value = claims.get(USER_ID_CLAIM);

        if (value instanceof Number number) {
            return number.longValue();
        }

        if (value instanceof String stringValue) {
            try {
                return Long.valueOf(stringValue);
            } catch (NumberFormatException exception) {
                throw new InvalidAccessTokenException(
                        "Access token contains an invalid user ID",
                        exception
                );
            }
        }

        throw new InvalidAccessTokenException(
                "Access token does not contain a valid user ID"
        );
    }

    private Set<String> extractRoles(Claims claims) {
        Object value = claims.get(ROLES_CLAIM);

        if (!(value instanceof List<?> roleValues)) {
            throw new InvalidAccessTokenException(
                    "Access token does not contain valid roles"
            );
        }

        Set<String> roles = new LinkedHashSet<>();

        for (Object roleValue : roleValues) {
            if (!(roleValue instanceof String roleName)
                    || roleName.isBlank()) {
                throw new InvalidAccessTokenException(
                        "Access token contains an invalid role"
                );
            }

            roles.add(roleName.trim().toUpperCase());
        }

        return Set.copyOf(roles);
    }

    private void validateRequiredClaims(
            String tokenId,
            Long userId,
            String username,
            Date issuedAt,
            Date expiration
    ) {
        if (tokenId == null || tokenId.isBlank()) {
            throw new InvalidAccessTokenException(
                    "Access token ID is missing"
            );
        }

        if (userId == null) {
            throw new InvalidAccessTokenException(
                    "Access token user ID is missing"
            );
        }

        if (username == null || username.isBlank()) {
            throw new InvalidAccessTokenException(
                    "Access token subject is missing"
            );
        }

        if (issuedAt == null) {
            throw new InvalidAccessTokenException(
                    "Access token issued time is missing"
            );
        }

        if (expiration == null) {
            throw new InvalidAccessTokenException(
                    "Access token expiration is missing"
            );
        }
    }

    private void validateRawToken(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidAccessTokenException(
                    "Access token must not be blank"
            );
        }
    }

    private void validateProperties(
            JwtProperties jwtProperties
    ) {
        if (jwtProperties == null) {
            throw new IllegalArgumentException(
                    "JWT properties must not be null"
            );
        }

        if (jwtProperties.secret() == null
                || jwtProperties.secret().isBlank()) {
            throw new IllegalStateException(
                    "JWT secret must be configured"
            );
        }

        if (jwtProperties.accessTokenExpirationMs() <= 0) {
            throw new IllegalStateException(
                    "JWT expiration must be greater than zero"
            );
        }
    }
}