package org.example.librarymanagement.domain.policies;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BorrowSlipCreationPolicyTest {

    @Test
    @DisplayName("validateCanBorrow - Success when reader card is ACTIVE and active")
    void givenActiveReader_whenValidateCanBorrow_thenDoNotThrow() {
        Reader reader = Reader.builder()
                .cardNumber("RD-001")
                .name("Reader Active")
                .email("active@test.com")
                .phoneNumber("0912345678")
                .address("Ha Noi")
                .cardStatus(CardStatus.ACTIVE)
                .cardIssuedAt(LocalDate.now())
                .cardExpiryAt(LocalDate.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        assertDoesNotThrow(() -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
    }

    @Test
    @DisplayName("validateCanBorrow - Throw exception when reader card is LOCKED")
    void givenLockedReader_whenValidateCanBorrow_thenThrowDomainException() {
        Reader reader = Reader.builder()
                .cardNumber("RD-001")
                .name("Reader Locked")
                .email("locked@test.com")
                .phoneNumber("0912345678")
                .address("Ha Noi")
                .cardStatus(CardStatus.LOCKED)
                .cardIssuedAt(LocalDate.now())
                .cardExpiryAt(LocalDate.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
    }

    @Test
    @DisplayName("validateCanBorrow - Throw exception when reader card is EXPIRED")
    void givenExpiredReader_whenValidateCanBorrow_thenThrowDomainException() {
        Reader reader = Reader.builder()
                .cardNumber("RD-001")
                .name("Reader Expired")
                .email("expired@test.com")
                .phoneNumber("0912345678")
                .address("Ha Noi")
                .cardStatus(CardStatus.EXPIRED)
                .cardIssuedAt(LocalDate.now())
                .cardExpiryAt(LocalDate.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
    }

    @Test
    @DisplayName("validateCanBorrow - Throw exception when reader is inactive")
    void givenInactiveReader_whenValidateCanBorrow_thenThrowDomainException() {
        Reader reader = Reader.builder()
                .cardNumber("RD-001")
                .name("Reader Inactive")
                .email("inactive@test.com")
                .phoneNumber("0912345678")
                .address("Ha Noi")
                .cardStatus(CardStatus.ACTIVE)
                .cardIssuedAt(LocalDate.now())
                .cardExpiryAt(LocalDate.now().plusYears(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(false)
                .build();

        assertThrows(DomainException.class, () -> BorrowSlipCreationPolicy.validateCanBorrow(reader));
    }
}
