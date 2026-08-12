package org.example.librarymanagement.port.dtos.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;

public record BorrowReportItemDto(
        Long borrowSlipId,
        String borrowCode,
        String readerCardCode,
        String readerName,
        String bookTitles,
        long totalBooks,
        LocalDateTime borrowedAt,
        LocalDateTime dueAt,
        LocalDateTime actualReturnedAt,
        BorrowSlipStatus status,
        BigDecimal fineAmount,
        String createdByUserName
) {
}
