package org.example.librarymanagement.application.auth;
import org.example.librarymanagement.application.auth.AccountManagementService; 
// hoặc package chứa AccountManagementService thực tế của bạn
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;


import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountManagementServiceTest {
   private LoadUserPort loadUserPort;
   private AccountManagementService accountManagementService;
   
   @BeforeEach
    void setUp() {
        loadUserPort = mock(LoadUserPort.class);
        accountManagementService = new AccountManagementService(loadUserPort);
    }

    @Test
    void locksAccountSuccessfully() {
        // Given
        User user = activeUser();

        when(loadUserPort.findById(1L))
                .thenReturn(Optional.of(user));

        // When
        accountManagementService.lockAccount(1L, "Vi phạm nội quy");

        // Then
        assertFalse(user.isEnabled());

        verify(loadUserPort).findById(1L);
        verify(loadUserPort).save(user);
    }

    @Test
    void unlocksAccountSuccessfully() {
        // Given
        User user = lockedUser();

        when(loadUserPort.findById(1L))
                .thenReturn(Optional.of(user));

        // When
        accountManagementService.unlockAccount(1L);

        // Then
        assertTrue(user.isEnabled());

        verify(loadUserPort).findById(1L);
        verify(loadUserPort).save(user);
    }

    @Test
    void rejectsLockingUnknownUser() {
        // Given
        when(loadUserPort.findById(100L))
                .thenReturn(Optional.empty());

        // When & Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> accountManagementService.lockAccount(100L, "Spam")
        );

        assertEquals(
                "Không tìm thấy người dùng với ID: 100",
                exception.getMessage()
        );

        verify(loadUserPort).findById(100L);
        verify(loadUserPort, never()).save(any());
    }

    @Test
    void rejectsUnlockingUnknownUser() {
        // Given
        when(loadUserPort.findById(200L))
                .thenReturn(Optional.empty());

        // When & Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> accountManagementService.unlockAccount(200L)
        );

        assertEquals(
                "Không tìm thấy người dùng với ID: 200",
                exception.getMessage()
        );

        verify(loadUserPort).findById(200L);
        verify(loadUserPort, never()).save(any());
    }

    private User activeUser() {
        LocalDateTime now = LocalDateTime.now();

        return new User(
                1L,
                "alice",
                "password",
                "Alice",
                "alice@test.com",
                "0123456789",
                true,
                null,
                now,
                now,
                Set.of(
                        new Role(1L, "admin", "Admin")
                )
        );
    }

    private User lockedUser() {
        User user = activeUser();
        user.deactivate();
        return user;
    }

}
