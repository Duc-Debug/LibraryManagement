package org.example.librarymanagement.infrastructure.security;

import java.io.IOException;
import java.util.List;

import org.example.librarymanagement.port.outbound.auth.AccessTokenRevocationPort;
import org.example.librarymanagement.port.outbound.auth.AccessTokenVerifierPort;
import org.example.librarymanagement.port.outbound.auth.token.InvalidAccessTokenException;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AccessTokenAuthenticationFilter extends OncePerRequestFilter {

    private final AccessTokenVerifierPort accessTokenVerifierPort;
    private final AccessTokenRevocationPort accessTokenRevocationPort;

    public AccessTokenAuthenticationFilter(
            AccessTokenVerifierPort accessTokenVerifierPort,
            AccessTokenRevocationPort accessTokenRevocationPort
    ) {
        this.accessTokenVerifierPort = accessTokenVerifierPort;
        this.accessTokenRevocationPort = accessTokenRevocationPort;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        return HttpMethod.OPTIONS.matches(request.getMethod())
                || "/error".equals(path)
                || isPost(path, request, "/api/auth/login")
                || isPost(path, request, "/api/auth/logout");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        try {
            VerifiedAccessToken verifiedToken = accessTokenVerifierPort.verify(token);

            if (accessTokenRevocationPort.isRevoked(verifiedToken.tokenId())) {
                unauthorized(response, "TOKEN_REVOKED");
                return;
            }

            List<SimpleGrantedAuthority> authorities = verifiedToken.roles()
                    .stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            UserPrincipal principal = new UserPrincipal(
                    verifiedToken.userId(),
                    verifiedToken.username(),
                    "",
                    authorities
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            principal,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (InvalidAccessTokenException exception) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "INVALID_TOKEN");
        }
    }

    private boolean isPost(
            String path,
            HttpServletRequest request,
            String expectedPath
    ) {
        return expectedPath.equals(path)
                && HttpMethod.POST.matches(request.getMethod());
    }

    private void unauthorized(
            HttpServletResponse response,
            String code
    ) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"code\":\"" + code + "\",\"message\":\"Unauthorized\"}"
        );
    }
}