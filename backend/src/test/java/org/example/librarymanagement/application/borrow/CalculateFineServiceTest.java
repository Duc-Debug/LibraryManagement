package org.example.librarymanagement.application.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.borrow.BorrowSlipNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
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
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CalculateFineServiceTest {

    @Mock
    private LoadBorrowSlipPort loadBorrowSlipPort;

    @Mock
    private GetAuthenticatedUserPort getAuthenticatedUserPort;

    private CalculateFineService calculateFineService;

    @BeforeEach
  public   void setUp() {
        calculateFineService = new CalculateFineService(loadBorrowSlipPort, getAuthenticatedUserPort);
    }

    private User createLibrarianUser() {
        Role librarianRole = new Role(2L, "LIBRARIAN", "Librarian Role");
        return new User(
                1L,
                "librarian1",
                "hashedpassword",
                "Thủ thư A",
                "librarian@library.org",
                "0123456789",
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                java.util.Set.of(librarianRole)
        );
    }

    private BorrowSlipResponseDto createSampleSlip(Long id, LocalDateTime borrowedAt, LocalDateTime dueAt, int totalBooks) {
        return new BorrowSlipResponseDto(
                id,
                "PM20260801-001",
                10L,
                "CARD-001",
                "Nguyễn Văn A",
                1L,
                "Thủ thư A",
                borrowedAt,
                dueAt,
                BorrowSlipStatus.BORROWING,
                "Ghi chú",
                totalBooks,
                borrowedAt
        );
    }

    @Test
    @DisplayName("calculateBorrowSlipFine: Tính đúng tiền phạt khi trả quá hạn 3 ngày")
    void calculateFine_Overdue_Success() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());

        LocalDateTime borrowedAt = LocalDateTime.of(2026, 8, 1, 10, 0);
        LocalDateTime dueAt = LocalDateTime.of(2026, 8, 15, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 18, 10, 0);

        BorrowSlipResponseDto sampleSlip = createSampleSlip(1L, borrowedAt, dueAt, 2);
        when(loadBorrowSlipPort.findSlipDetailById(1L)).thenReturn(Optional.of(sampleSlip));

        // Act
        FineCalculationResponseDto result = calculateFineService.calculateBorrowSlipFine(1L, returnDate);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.borrowSlipId());
        assertEquals("PM20260801-001", result.borrowCode());
        assertEquals(3L, result.overdueDays());
        assertTrue(result.isOverdue());
        assertEquals(BigDecimal.valueOf(30000), result.totalFineAmount());
        assertNotNull(result.fineReason());

        verify(loadBorrowSlipPort).findSlipDetailById(1L);
    }

    @Test
    @DisplayName("calculateBorrowSlipFine: Tiền phạt = 0 khi trả đúng hạn")
    void calculateFine_OnTime_ReturnsZeroFine() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());

        LocalDateTime borrowedAt = LocalDateTime.of(2026, 8, 1, 10, 0);
        LocalDateTime dueAt = LocalDateTime.of(2026, 8, 15, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 14, 10, 0);

        BorrowSlipResponseDto sampleSlip = createSampleSlip(1L, borrowedAt, dueAt, 2);
        when(loadBorrowSlipPort.findSlipDetailById(1L)).thenReturn(Optional.of(sampleSlip));

        // Act
        FineCalculationResponseDto result = calculateFineService.calculateBorrowSlipFine(1L, returnDate);

        // Assert
        assertEquals(0L, result.overdueDays());
        assertFalse(result.isOverdue());
        assertEquals(BigDecimal.ZERO, result.totalFineAmount());
        assertNull(result.fineReason());
    }

    @Test
    @DisplayName("calculateBorrowSlipFine: Ném BorrowSlipNotFoundException khi phiếu không tồn tại")
    void calculateFine_NotFound_ThrowsException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        when(loadBorrowSlipPort.findSlipDetailById(999L)).thenReturn(Optional.empty());

        BorrowSlipNotFoundException exception = assertThrows(BorrowSlipNotFoundException.class, () ->
                calculateFineService.calculateBorrowSlipFine(999L, null));
        assertEquals("Borrow slip not found with ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("calculateBorrowSlipFine: Ném ValidationException khi ID <= 0")
    void calculateFine_InvalidId_ThrowsValidationException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());

        ValidationException exZero = assertThrows(ValidationException.class, () ->
                calculateFineService.calculateBorrowSlipFine(0L, null));
        assertEquals("Borrow slip ID must be greater than 0", exZero.getMessage());

        ValidationException exNegative = assertThrows(ValidationException.class, () ->
                calculateFineService.calculateBorrowSlipFine(-5L, null));
        assertEquals("Borrow slip ID must be greater than 0", exNegative.getMessage());
    }

    @Test
    @DisplayName("calculateBorrowSlipFine: Ném UnauthenticatedException khi chưa đăng nhập")
    void calculateFine_Unauthenticated_ThrowsException() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(null);

        UnauthenticatedException exception = assertThrows(UnauthenticatedException.class, () ->
                calculateFineService.calculateBorrowSlipFine(1L, null));
        assertEquals("User is unauthenticated", exception.getMessage());
    }
}
