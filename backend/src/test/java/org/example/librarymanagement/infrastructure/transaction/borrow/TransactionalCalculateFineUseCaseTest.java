package org.example.librarymanagement.infrastructure.transaction.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TransactionalCalculateFineUseCaseTest {

    @Mock
    private CalculateFineUseCase delegate;

    @Test
    @DisplayName("TransactionalCalculateFineUseCase: Ném NullPointerException khi delegate null")
    void constructor_NullDelegate_ThrowsException() {
        assertThrows(NullPointerException.class, () -> new TransactionalCalculateFineUseCase(null));
    }

    @Test
    @DisplayName("calculateBorrowSlipFine: Ủy quyền chính xác sang delegate")
    void calculateBorrowSlipFine_DelegatesProperly() {
        // Arrange
        TransactionalCalculateFineUseCase useCase = new TransactionalCalculateFineUseCase(delegate);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 20, 10, 0);

        FineCalculationResponseDto mockDto = new FineCalculationResponseDto(
                1L, "PM001", 10L, "RD01", "Nguyễn Văn A",
                LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(2),
                returnDate, 2, 1, BigDecimal.valueOf(5000),
                BigDecimal.valueOf(10000), BigDecimal.valueOf(10000), true, "Quá hạn 2 ngày"
        );

        when(delegate.calculateBorrowSlipFine(1L, returnDate)).thenReturn(mockDto);

        // Act
        FineCalculationResponseDto result = useCase.calculateBorrowSlipFine(1L, returnDate);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.borrowSlipId());
        assertEquals("PM001", result.borrowCode());
        verify(delegate).calculateBorrowSlipFine(1L, returnDate);
    }
}
