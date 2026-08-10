package org.example.librarymanagement.application.auth;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.shared.InvalidCredentialsException;
import org.example.librarymanagement.port.dtos.auth.LoginCommand;
import org.example.librarymanagement.port.dtos.auth.LoginResult;
import org.example.librarymanagement.port.outbound.auth.AccessTokenIssuerPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.token.AccessTokenPayload;
import org.example.librarymanagement.port.outbound.auth.token.IssuedAccessToken;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    private LoadUserPort loadUserPort;
    private PasswordVerifierPort passwordVerifierPort;
    
    private AccessTokenIssuerPort accessTokenIssuerPort;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loadUserPort = mock(LoadUserPort.class);
        passwordVerifierPort = mock(PasswordVerifierPort.class);
        accessTokenIssuerPort = mock(AccessTokenIssuerPort.class);
        loginService = new LoginService(
                loadUserPort,
                passwordVerifierPort,
                accessTokenIssuerPort
        );
    }

    @Test
    void rejectsNullCommand() {
        assertThrows(
                IllegalArgumentException.class,
                () -> loginService.login(null)
        );
    }

    @Test
    void rejectsBlankUsername() {
        assertThrows(
                IllegalArgumentException.class,
                () -> loginService.login(new LoginCommand(" ", "secret"))
        );
    }

    @Test
    void rejectsBlankPassword() {
        assertThrows(
                IllegalArgumentException.class,
                () -> loginService.login(new LoginCommand("alice", " "))
        );
    }

    @Test
    void rejectsUnknownUserWithoutGeneratingToken() {
        when(loadUserPort.findByUsername("alice"))
                .thenReturn(Optional.empty());

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand("alice", "secret"))
        );

        verify(accessTokenIssuerPort, never()).issue(any());
    }

    @Test
    void rejectsWrongPasswordWithoutGeneratingToken() {
        User user = user();
        when(loadUserPort.findByUsername("alice"))
                .thenReturn(Optional.of(user));
        when(passwordVerifierPort.matches("wrong", user.getPasswordHash()))
                .thenReturn(false);

        assertThrows(
                InvalidCredentialsException.class,
                () -> loginService.login(new LoginCommand("alice", "wrong"))
        );

        verify(accessTokenIssuerPort, never()).issue(any());
    }

    @Test
    void logsInAndMapsResult() {
        User user = user();
        when(loadUserPort.findByUsername("alice"))
                .thenReturn(Optional.of(user));
        when(passwordVerifierPort.matches("secret", user.getPasswordHash()))
                .thenReturn(true);
        when(accessTokenIssuerPort.issue(any(AccessTokenPayload.class)))
                .thenReturn(new IssuedAccessToken(
                        "jwt-token",
                        "token-id",
                        Instant.now(),
                        Instant.now().plusSeconds(3600)
                ));

        LoginResult result = loginService.login(
                new LoginCommand(" alice ", "secret")
        );

        assertEquals(1L, result.userId());
        assertEquals("alice", result.username());
        assertEquals("Alice Reader", result.fullName());
        assertEquals("jwt-token", result.accessToken());
        assertTrue(result.roles().contains("ADMIN"));
        assertTrue(result.roles().contains("LIBRARIAN"));
        verify(accessTokenIssuerPort).issue(argThat(payload ->
                payload.userId().equals(1L)
                        && payload.username().equals("alice")
                        && payload.roles().contains("ADMIN")
                        && payload.roles().contains("LIBRARIAN")
        ));
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
                Set.of(
                        new Role(1L, "admin", "Admin"),
                        new Role(2L, "librarian", "Librarian")
                )
        );
    }
}
