package org.example.librarymanagement.domain.policies;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.borrow.BorrowLimitExceededException;
import org.example.librarymanagement.domain.exceptions.borrow.ReaderHasOverdueBorrowException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BorrowSlipCreationPolicyTest {

    private Reader createReader(CardStatus cardStatus, boolean active) {
        return Reader.builder()
                .cardNumber("RD-001")
                .name("Reader Test")
                .email("test@example.com")
                .phoneNumber("0912345678")
                .address("Ha Noi")
                .cardStatus(cardStatus)
                .cardIssuedAt(LocalDate.now())
                .cardExpiryAt(LocalDate.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(active)
                .build();
    }

    // ==================== validateCanBorrow TESTS ====================

    @Test
    @DisplayName("validateCanBorrow - Success when reader card is ACTIVE and active")
    void givenActiveReader_whenValidateCanBorrow_thenDoNotThrow() {
        Reader reader = createReader(CardStatus.ACTIVE, true);
        assertDoesNotThrow(() -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
    }

    @Test
    @DisplayName("validateCanBorrow - Throw exception when reader is null")
    void givenNullReader_whenValidateCanBorrow_thenThrowDomainException() {
        DomainException ex = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateCanBorrow(null));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("validateCanBorrow - Throw exception when reader card is LOCKED")
    void givenLockedReader_whenValidateCanBorrow_thenThrowDomainException() {
        Reader reader = createReader(CardStatus.LOCKED, true);
        DomainException ex = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("validateCanBorrow - Throw exception when reader card is EXPIRED")
    void givenExpiredReader_whenValidateCanBorrow_thenThrowDomainException() {
        Reader reader = createReader(CardStatus.EXPIRED, true);
        DomainException ex = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("validateCanBorrow - Throw exception when reader is inactive")
    void givenInactiveReader_whenValidateCanBorrow_thenThrowDomainException() {
        Reader reader = createReader(CardStatus.ACTIVE, false);
        DomainException ex = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
        assertNotNull(ex.getMessage());
    }

    // ==================== validateNoOverdueBooks TESTS ====================

    @Test
    @DisplayName("validateNoOverdueBooks - Success when reader has NO overdue books")
    void givenNoOverdueBooks_whenValidateNoOverdueBooks_thenDoNotThrow() {
        assertDoesNotThrow(() -> BorrowSlipCreationPolicy.validateNoOverdueBooks(false, "RD-001"));
    }

    @Test
    @DisplayName("validateNoOverdueBooks - Throw ReaderHasOverdueBorrowException when reader has overdue books")
    void givenOverdueBooks_whenValidateNoOverdueBooks_thenThrowReaderHasOverdueBorrowException() {
        ReaderHasOverdueBorrowException ex = assertThrows(
                ReaderHasOverdueBorrowException.class,
                () -> BorrowSlipCreationPolicy.validateNoOverdueBooks(true, "RD-001")
        );
        assertEquals("RD-001", ex.getReaderCardNumber());
    }

    // ==================== validateBorrowLimit TESTS ====================

    @Test
    @DisplayName("validateBorrowLimit - Success when total books <= max limit")
    void givenValidBorrowCount_whenValidateBorrowLimit_thenDoNotThrow() {
        // 0 đang mượn + 3 mượn mới <= 5
        assertDoesNotThrow(() -> BorrowSlipCreationPolicy.validateBorrowLimit(0, 3, 5));

        // 3 đang mượn + 2 mượn mới == 5
        assertDoesNotThrow(() -> BorrowSlipCreationPolicy.validateBorrowLimit(3, 2, 5));

        // 4 đang mượn + 1 mượn mới == 5
        assertDoesNotThrow(() -> BorrowSlipCreationPolicy.validateBorrowLimit(4, 1, 5));
    }

    @Test
    @DisplayName("validateBorrowLimit - Throw BorrowLimitExceededException when total books > max limit")
    void givenExceededBorrowCount_whenValidateBorrowLimit_thenThrowBorrowLimitExceededException() {
        // 3 đang mượn + 3 mượn mới > 5
        BorrowLimitExceededException ex = assertThrows(
                BorrowLimitExceededException.class,
                () -> BorrowSlipCreationPolicy.validateBorrowLimit(3, 3, 5)
        );

        assertEquals(3, ex.getCurrentBorrowingCount());
        assertEquals(3, ex.getRequestedBooksCount());
        assertEquals(5, ex.getMaxLimit());

        // 5 đang mượn + 1 mượn mới > 5
        BorrowLimitExceededException ex2 = assertThrows(
                BorrowLimitExceededException.class,
                () -> BorrowSlipCreationPolicy.validateBorrowLimit(5, 1, 5)
        );
        assertEquals(5, ex2.getCurrentBorrowingCount());
        assertEquals(1, ex2.getRequestedBooksCount());
        assertEquals(5, ex2.getMaxLimit());
    }

    @Test
    @DisplayName("validateBorrowLimit - Throw DomainException when maxLimit <= 0")
    void givenInvalidMaxLimit_whenValidateBorrowLimit_thenThrowDomainException() {
        DomainException ex1 = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateBorrowLimit(0, 1, 0));
        assertNotNull(ex1.getMessage());
        DomainException ex2 = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateBorrowLimit(0, 1, -1));
        assertNotNull(ex2.getMessage());
    }

    @Test
    @DisplayName("validateBorrowLimit - Throw DomainException when currentBorrowingCount < 0")
    void givenNegativeCurrentBorrowing_whenValidateBorrowLimit_thenThrowDomainException() {
        DomainException ex = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateBorrowLimit(-1, 1, 5));
        assertNotNull(ex.getMessage());
    }

    @Test
    @DisplayName("validateBorrowLimit - Throw DomainException when requestedBooksCount <= 0")
    void givenInvalidRequestedBooksCount_whenValidateBorrowLimit_thenThrowDomainException() {
        DomainException ex1 = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateBorrowLimit(0, 0, 5));
        assertNotNull(ex1.getMessage());
        DomainException ex2 = assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateBorrowLimit(0, -1, 5));
        assertNotNull(ex2.getMessage());
    }
}
