package org.example.librarymanagement.port.dtos.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BorrowSlipItemResponseDto(
    Long id,
    Long bookId,
    String bookTitle,
    String isbn,
    String author,
    String categoryName,
    String coverUrl,
    LocalDateTime returnedAt,
    BigDecimal fineAmount,
    String fineReason
) {}
