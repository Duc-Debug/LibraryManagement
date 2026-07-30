package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.domain.auth.Role;
import org.example.librarymanagement.domain.auth.User;
import org.example.librarymanagement.port.inbound.auth.LoginCommand;
import org.example.librarymanagement.port.inbound.auth.LoginResult;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.TokenProviderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LoginServiceTest {

    private LoadUserPort loadUserPort;
    private PasswordVerifierPort passwordVerifierPort;
    private TokenProviderPort tokenProviderPort;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loadUserPort = mock(LoadUserPort.class);
        passwordVerifierPort = mock(PasswordVerifierPort.class);
        tokenProviderPort = mock(TokenProviderPort.class);
        loginService = new LoginService(
                loadUserPort,
                passwordVerifierPort,
                tokenProviderPort
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

        verify(tokenProviderPort, never()).generateAccessToken(
                org.mockito.ArgumentMatchers.any()
        );
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

        verify(tokenProviderPort, never()).generateAccessToken(user);
    }

    @Test
    void logsInAndMapsResult() {
        User user = user();
        when(loadUserPort.findByUsername("alice"))
                .thenReturn(Optional.of(user));
        when(passwordVerifierPort.matches("secret", user.getPasswordHash()))
                .thenReturn(true);
        when(tokenProviderPort.generateAccessToken(user))
                .thenReturn("jwt-token");

        LoginResult result = loginService.login(
                new LoginCommand(" alice ", "secret")
        );

        assertEquals(1L, result.userId());
        assertEquals("alice", result.username());
        assertEquals("Alice Reader", result.fullName());
        assertEquals("jwt-token", result.accessToken());
        assertTrue(result.roles().contains("ADMIN"));
        assertTrue(result.roles().contains("LIBRARIAN"));
        verify(tokenProviderPort).generateAccessToken(user);
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
