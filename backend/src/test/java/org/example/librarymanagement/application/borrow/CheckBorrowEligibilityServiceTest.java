package org.example.librarymanagement.application.borrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.borrow.BorrowLimitExceededException;
import org.example.librarymanagement.domain.exceptions.borrow.ReaderHasOverdueBorrowException;
import org.example.librarymanagement.domain.exceptions.reader.ReaderNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.AccessDeniedException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.port.dtos.borrow.OverdueSlipSummaryDto;
import org.example.librarymanagement.port.dtos.borrow.ReaderBorrowEligibilityDto;
import org.example.librarymanagement.port.outbound.borrow.LoadReaderBorrowStatusPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.setting.LoadSystemSettingPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckBorrowEligibilityServiceTest {

    @Mock
    private ReaderRepositoryPort readerRepositoryPort;

    @Mock
    private LoadReaderBorrowStatusPort loadReaderBorrowStatusPort;

    @Mock
    private LoadSystemSettingPort loadSystemSettingPort;

    @Mock
    private GetAuthenticatedUserPort getAuthenticatedUserPort;

    private CheckBorrowEligibilityService service;

    @BeforeEach
    void setUp() {
        service = new CheckBorrowEligibilityService(
                readerRepositoryPort,
                loadReaderBorrowStatusPort,
                loadSystemSettingPort,
                getAuthenticatedUserPort
        );
    }

    private User createLibrarianUser() {
        Role librarianRole = new Role(2L, "LIBRARIAN", "Librarian Role");
        return new User(
                1L,
                "librarian1",
                "hashedpassword",
                "Thủ thư Nguyễn Văn A",
                "librarian@library.org",
                "0123456789",
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(librarianRole)
        );
    }

    private Reader createReader(Long id, CardStatus cardStatus, boolean active) {
        return Reader.builder()
                .id(id)
                .cardNumber("RD-0001")
                .name("Độc giả Trần Văn B")
                .email("reader.b@example.com")
                .phoneNumber("0987654321")
                .address("123 Phố Sách, Hà Nội")
                .cardStatus(cardStatus)
                .cardIssuedAt(LocalDate.now().minusMonths(1))
                .cardExpiryAt(LocalDate.now().plusMonths(11))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(active)
                .build();
    }

    // ==================== checkEligibility TESTS ====================

    @Test
    @DisplayName("checkEligibility - Success when reader is fully eligible with remaining limit")
    void givenEligibleReader_whenCheckEligibility_thenReturnEligibleDto() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.ACTIVE, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadSystemSettingPort.getIntSetting(eq(org.example.librarymanagement.domain.constant.SystemSettingKeys.MAX_CONCURRENT_BORROW_BOOKS), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(2);
        when(loadReaderBorrowStatusPort.findOverdueSlipsByReaderId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());

        ReaderBorrowEligibilityDto result = service.checkEligibility(1L);

        assertNotNull(result);
        assertTrue(result.eligible());
        assertEquals(1L, result.readerId());
        assertEquals("RD-0001", result.cardNumber());
        assertEquals("Độc giả Trần Văn B", result.readerName());
        assertEquals(2, result.currentBorrowingCount());
        assertEquals(5, result.maxBorrowLimit());
        assertEquals(3, result.remainingBorrowLimit());
        assertFalse(result.hasOverdueBooks());
        assertEquals(0, result.overdueBooksCount());
        assertNull(result.rejectionReason());
    }

    @Test
    @DisplayName("checkEligibility - Rejected when reader card is LOCKED")
    void givenLockedCardReader_whenCheckEligibility_thenReturnRejectedDto() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.LOCKED, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadSystemSettingPort.getIntSetting(anyString(), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(1);

        ReaderBorrowEligibilityDto result = service.checkEligibility(1L);

        assertNotNull(result);
        assertFalse(result.eligible());
        assertTrue(result.rejectionReason().contains("LOCKED"));
    }

    @Test
    @DisplayName("checkEligibility - Rejected when reader card is EXPIRED")
    void givenExpiredCardReader_whenCheckEligibility_thenReturnRejectedDto() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.EXPIRED, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadSystemSettingPort.getIntSetting(anyString(), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(1);

        ReaderBorrowEligibilityDto result = service.checkEligibility(1L);

        assertNotNull(result);
        assertFalse(result.eligible());
        assertTrue(result.rejectionReason().contains("EXPIRED"));
    }

    @Test
    @DisplayName("checkEligibility - Rejected when reader account is inactive")
    void givenInactiveReader_whenCheckEligibility_thenReturnRejectedDto() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.ACTIVE, false);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadSystemSettingPort.getIntSetting(anyString(), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(0);

        ReaderBorrowEligibilityDto result = service.checkEligibility(1L);

        assertNotNull(result);
        assertFalse(result.eligible());
        assertTrue(result.rejectionReason().contains("vô hiệu hóa"));
    }

    @Test
    @DisplayName("checkEligibility - Rejected when reader has overdue books")
    void givenOverdueBooksReader_whenCheckEligibility_thenReturnRejectedDto() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.ACTIVE, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadSystemSettingPort.getIntSetting(anyString(), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(3);

        OverdueSlipSummaryDto overdueSlip = new OverdueSlipSummaryDto(
                10L, "BR-2026-0001", LocalDateTime.now().minusDays(5), 5L, 2
        );
        when(loadReaderBorrowStatusPort.findOverdueSlipsByReaderId(eq(1L), any(LocalDateTime.class)))
                .thenReturn(List.of(overdueSlip));

        ReaderBorrowEligibilityDto result = service.checkEligibility(1L);

        assertNotNull(result);
        assertFalse(result.eligible());
        assertTrue(result.hasOverdueBooks());
        assertEquals(2, result.overdueBooksCount());
        assertEquals(1, result.overdueSlips().size());
        assertTrue(result.rejectionReason().contains("quá hạn"));
    }

    @Test
    @DisplayName("checkEligibility - Rejected when reader reached maximum borrow limit")
    void givenMaxBorrowLimitReached_whenCheckEligibility_thenReturnRejectedDto() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.ACTIVE, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadSystemSettingPort.getIntSetting(anyString(), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(5);
        when(loadReaderBorrowStatusPort.findOverdueSlipsByReaderId(eq(1L), any(LocalDateTime.class))).thenReturn(List.of());

        ReaderBorrowEligibilityDto result = service.checkEligibility(1L);

        assertNotNull(result);
        assertFalse(result.eligible());
        assertEquals(0, result.remainingBorrowLimit());
        assertTrue(result.rejectionReason().contains("hạn mức mượn sách tối đa"));
    }

    @Test
    @DisplayName("checkEligibility - Throw ReaderNotFoundException when reader id not found")
    void givenNonExistingReader_whenCheckEligibility_thenThrowReaderNotFoundException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        when(readerRepositoryPort.findById(999L)).thenReturn(Optional.empty());

        ReaderNotFoundException ex = assertThrows(ReaderNotFoundException.class, () -> service.checkEligibility(999L));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("checkEligibility - Throw ValidationException when reader id is null or <= 0")
    void givenInvalidReaderId_whenCheckEligibility_thenThrowValidationException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());

        ValidationException ex1 = assertThrows(ValidationException.class, () -> service.checkEligibility(null));
        assertNotNull(ex1.getMessage());

        ValidationException ex2 = assertThrows(ValidationException.class, () -> service.checkEligibility(0L));
        assertNotNull(ex2.getMessage());

        ValidationException ex3 = assertThrows(ValidationException.class, () -> service.checkEligibility(-1L));
        assertNotNull(ex3.getMessage());
    }

    @Test
    @DisplayName("checkEligibility - Throw UnauthenticatedException when user is unauthenticated")
    void givenUnauthenticated_whenCheckEligibility_thenThrowUnauthenticatedException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(null);

        UnauthenticatedException ex = assertThrows(UnauthenticatedException.class, () -> service.checkEligibility(1L));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("checkEligibility - Throw AccessDeniedException when user is not staff")
    void givenNonStaffUser_whenCheckEligibility_thenThrowAccessDeniedException() {
        Role guestRole = new Role(3L, "MEMBER", "Member Role");
        User guestUser = new User(
                2L, "guest", "pwd", "Guest User", "guest@test.com", "0123", true, null,
                LocalDateTime.now(), LocalDateTime.now(), Set.of(guestRole)
        );
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(guestUser);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> service.checkEligibility(1L));
        assertNotNull(ex.getMessage());
    }

    // ==================== validateBorrowEligibility TESTS ====================

    @Test
    @DisplayName("validateBorrowEligibility - Success when valid request within limit and no overdue")
    void givenValidBorrowRequest_whenValidateBorrowEligibility_thenDoNotThrow() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.ACTIVE, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadReaderBorrowStatusPort.hasOverdueBooks(eq(1L), any(LocalDateTime.class))).thenReturn(false);
        when(loadSystemSettingPort.getIntSetting(anyString(), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(2);

        // Đang mượn 2, mượn thêm 2 -> tổng 4 <= 5 -> Hợp lệ
        service.validateBorrowEligibility(1L, 2);

        verify(readerRepositoryPort).findById(1L);
        verify(loadReaderBorrowStatusPort).hasOverdueBooks(eq(1L), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("validateBorrowEligibility - Throw ReaderHasOverdueBorrowException when overdue exists")
    void givenOverdueBooks_whenValidateBorrowEligibility_thenThrowReaderHasOverdueBorrowException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.ACTIVE, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadReaderBorrowStatusPort.hasOverdueBooks(eq(1L), any(LocalDateTime.class))).thenReturn(true);

        ReaderHasOverdueBorrowException ex = assertThrows(
                ReaderHasOverdueBorrowException.class,
                () -> service.validateBorrowEligibility(1L, 1)
        );
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("validateBorrowEligibility - Throw BorrowLimitExceededException when requested + current > limit")
    void givenExceededLimit_whenValidateBorrowEligibility_thenThrowBorrowLimitExceededException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        Reader reader = createReader(1L, CardStatus.ACTIVE, true);
        when(readerRepositoryPort.findById(1L)).thenReturn(Optional.of(reader));
        when(loadReaderBorrowStatusPort.hasOverdueBooks(eq(1L), any(LocalDateTime.class))).thenReturn(false);
        when(loadSystemSettingPort.getIntSetting(anyString(), anyInt())).thenReturn(5);
        when(loadReaderBorrowStatusPort.countCurrentBorrowingBooks(1L)).thenReturn(3);

        // Đang mượn 3, mượn thêm 3 -> tổng 6 > 5 -> Chặn mượn
        BorrowLimitExceededException ex = assertThrows(
                BorrowLimitExceededException.class,
                () -> service.validateBorrowEligibility(1L, 3)
        );
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("validateBorrowEligibility - Throw ValidationException on invalid requestedBooksCount")
    void givenInvalidRequestedCount_whenValidateBorrowEligibility_thenThrowValidationException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());

        ValidationException ex1 = assertThrows(ValidationException.class, () -> service.validateBorrowEligibility(1L, 0));
        assertNotNull(ex1.getMessage());

        ValidationException ex2 = assertThrows(ValidationException.class, () -> service.validateBorrowEligibility(1L, -2));
        assertNotNull(ex2.getMessage());
    }
}
