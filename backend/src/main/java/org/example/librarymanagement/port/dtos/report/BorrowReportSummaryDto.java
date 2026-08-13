package org.example.librarymanagement.port.dtos.report;

import java.math.BigDecimal;

public record BorrowReportSummaryDto(
        long totalSlips,
        long totalBooksBorrowed,
        long totalReturned,
        long totalOverdue,
        long totalBorrowing,
        BigDecimal totalFineAmount
) {
}
