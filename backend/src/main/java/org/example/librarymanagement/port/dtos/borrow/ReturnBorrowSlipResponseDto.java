package org.example.librarymanagement.port.dtos.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;

public record ReturnBorrowSlipResponseDto(
        Long borrowSlipId,
        BorrowSlipStatus status,
        int returnedBooks,
        LocalDateTime returnedAt,
        BigDecimal totalFineAmount
) {
}