package org.example.librarymanagement.port.dtos.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO trả về kết quả tính toán phí phạt chi tiết
 */
public record FineCalculationResponseDto(
        Long borrowSlipId,
        String borrowCode,
        Long readerId,
        String readerCardNumber,
        String readerName,
        LocalDateTime borrowedAt,
        LocalDateTime dueAt,
        LocalDateTime returnDate,
        long overdueDays,
        int totalBooks,
        BigDecimal dailyFineRate,
        BigDecimal overdueFineAmount,
        BigDecimal totalFineAmount,
        boolean isOverdue,
        String fineReason
) {
}