package org.example.librarymanagement.port.dtos.borrow;

import java.time.LocalDateTime;

public record OverdueSlipSummaryDto(
        Long slipId,
        String borrowCode,
        LocalDateTime dueAt,
        long overdueDays,
        int unreturnedBooksCount
) {
}
