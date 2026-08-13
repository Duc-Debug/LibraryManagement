package org.example.librarymanagement.port.dtos.report;

import java.time.LocalDate;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;

public record BorrowReportQuery(
        LocalDate startDate,
        LocalDate endDate,
        BorrowSlipStatus status,
        String keyword,
        int page,
        int size
) {
    public static BorrowReportQuery of(LocalDate startDate, LocalDate endDate, BorrowSlipStatus status, String keyword, int page, int size) {
        return new BorrowReportQuery(startDate, endDate, status, keyword, page, size);
    }
}
