package org.example.librarymanagement.application.setting;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.application.borrow.CheckBorrowEligibilityService;
import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.SystemSetting;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.setting.SystemSettingNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.AccessDeniedException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.port.dtos.setting.SystemSettingResponseDto;
import org.example.librarymanagement.port.outbound.setting.SaveSystemSettingPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ManageSystemSettingServiceTest {

    @Mock
    private SaveSystemSettingPort saveSystemSettingPort;

    @Mock
    private GetAuthenticatedUserPort getAuthenticatedUserPort;

    private ManageSystemSettingService service;

    @BeforeEach
    void setUp() {
        service = new ManageSystemSettingService(saveSystemSettingPort, getAuthenticatedUserPort);
    }

    private User createAdminUser() {
        Role adminRole = new Role(1L, "ADMIN", "Administrator");
        return new User(
                1L,
                "admin1",
                "hashedpassword",
                "Quản trị viên",
                "admin@library.org",
                "0123456789",
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(adminRole)
        );
    }

    private User createLibrarianUser() {
        Role librarianRole = new Role(2L, "LIBRARIAN", "Librarian");
        return new User(
                2L,
                "librarian1",
                "hashedpassword",
                "Thủ thư",
                "librarian@library.org",
                "0123456788",
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(librarianRole)
        );
    }

    // ==================== getAllSettings TESTS ====================

    @Test
    @DisplayName("getAllSettings - Success for staff users (Librarian/Admin)")
    void givenStaffUser_whenGetAllSettings_thenReturnList() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        SystemSetting setting = new SystemSetting(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Giới hạn mượn", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(saveSystemSettingPort.findAll()).thenReturn(List.of(setting));

        List<SystemSettingResponseDto> result = service.getAllSettings();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MAX_CONCURRENT_BORROW_BOOKS", result.get(0).settingKey());
        assertEquals("5", result.get(0).settingValue());
    }

    // ==================== getSettingByKey TESTS ====================

    @Test
    @DisplayName("getSettingByKey - Success when key exists")
    void givenExistingKey_whenGetSettingByKey_thenReturnDto() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        SystemSetting setting = new SystemSetting(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Giới hạn mượn", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(saveSystemSettingPort.findBySettingKey("MAX_CONCURRENT_BORROW_BOOKS")).thenReturn(Optional.of(setting));

        SystemSettingResponseDto result = service.getSettingByKey("MAX_CONCURRENT_BORROW_BOOKS");

        assertNotNull(result);
        assertEquals("MAX_CONCURRENT_BORROW_BOOKS", result.settingKey());
        assertEquals("5", result.settingValue());
    }

    @Test
    @DisplayName("getSettingByKey - Throw SystemSettingNotFoundException when key not found")
    void givenNonExistingKey_whenGetSettingByKey_thenThrowNotFound() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        when(saveSystemSettingPort.findBySettingKey("NON_EXISTING")).thenReturn(Optional.empty());

        SystemSettingNotFoundException ex = assertThrows(
                SystemSettingNotFoundException.class,
                () -> service.getSettingByKey("NON_EXISTING")
        );
        assertNotNull(ex.getMessage());
    }

    // ==================== updateSetting TESTS (ADMIN ONLY) ====================

    @Test
    @DisplayName("updateSetting - Success when user is ADMIN")
    void givenAdminUser_whenUpdateSetting_thenUpdateAndSaveSuccessfully() {
        User admin = createAdminUser();
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(admin);

        SystemSetting existing = new SystemSetting(
                1L, "MAX_CONCURRENT_BORROW_BOOKS", "5", "Mô tả cũ", 1L, LocalDateTime.now(), LocalDateTime.now()
        );
        when(saveSystemSettingPort.findBySettingKey("MAX_CONCURRENT_BORROW_BOOKS")).thenReturn(Optional.of(existing));
        when(saveSystemSettingPort.save(any(SystemSetting.class))).thenAnswer(inv -> inv.getArgument(0));

        SystemSettingResponseDto result = service.updateSetting("MAX_CONCURRENT_BORROW_BOOKS", "7", "Cập nhật mới 7 cuốn");

        assertNotNull(result);
        assertEquals("MAX_CONCURRENT_BORROW_BOOKS", result.settingKey());
        assertEquals("7", result.settingValue());
        assertEquals("Cập nhật mới 7 cuốn", result.description());
        assertEquals(admin.getId(), result.updatedByUserId());
        verify(saveSystemSettingPort).save(existing);
    }

    @Test
    @DisplayName("updateSetting - Throw AccessDeniedException when user is LIBRARIAN (not Admin)")
    void givenLibrarianUser_whenUpdateSetting_thenThrowAccessDeniedException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> service.updateSetting("MAX_CONCURRENT_BORROW_BOOKS", "7", "Update")
        );

        assertNotNull(ex.getMessage());
        verify(saveSystemSettingPort, never()).save(any());
    }

    @Test
    @DisplayName("updateSetting - Throw UnauthenticatedException when user is not logged in")
    void givenUnauthenticated_whenUpdateSetting_thenThrowUnauthenticatedException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(null);

        UnauthenticatedException ex = assertThrows(
                UnauthenticatedException.class,
                () -> service.updateSetting("MAX_CONCURRENT_BORROW_BOOKS", "7", "Update")
        );
        assertNotNull(ex.getMessage());
        verify(saveSystemSettingPort, never()).save(any());
    }

    @Test
    @DisplayName("updateSetting - Throw ValidationException when MAX_CONCURRENT_BORROW_BOOKS value is invalid")
    void givenInvalidBorrowLimitValue_whenUpdateSetting_thenThrowValidationException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createAdminUser());

        // Giá trị <= 0
        ValidationException ex1 = assertThrows(
                ValidationException.class,
                () -> service.updateSetting(CheckBorrowEligibilityService.SETTING_KEY_MAX_BORROW_LIMIT, "0", "Limit 0")
        );
        assertNotNull(ex1.getMessage());

        ValidationException ex2 = assertThrows(
                ValidationException.class,
                () -> service.updateSetting(CheckBorrowEligibilityService.SETTING_KEY_MAX_BORROW_LIMIT, "-3", "Limit -3")
        );
        assertNotNull(ex2.getMessage());

        // Không phải số
        ValidationException ex3 = assertThrows(
                ValidationException.class,
                () -> service.updateSetting(CheckBorrowEligibilityService.SETTING_KEY_MAX_BORROW_LIMIT, "abc", "Invalid number")
        );
        assertNotNull(ex3.getMessage());
    }

    @Test
    @DisplayName("updateSetting - Throw ValidationException when key or value is blank")
    void givenBlankKeyOrValue_whenUpdateSetting_thenThrowValidationException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createAdminUser());

        ValidationException ex1 = assertThrows(ValidationException.class, () -> service.updateSetting("", "5", "Desc"));
        assertNotNull(ex1.getMessage());

        ValidationException ex2 = assertThrows(ValidationException.class, () -> service.updateSetting("KEY", "", "Desc"));
        assertNotNull(ex2.getMessage());
    }
}

