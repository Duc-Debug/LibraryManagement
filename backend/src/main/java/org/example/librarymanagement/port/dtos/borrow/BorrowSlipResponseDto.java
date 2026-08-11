package org.example.librarymanagement.port.dtos.borrow;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;

public record BorrowSlipResponseDto(
    Long id,
    String borrowCode,
    Long readerId,
    String readerCardNumber,
    String readerName,
    Long createdByUserId,
    String createdByUserName,
    LocalDateTime borrowedAt,
    LocalDateTime dueAt,
    BorrowSlipStatus status,
    String note,
    int totalBooks,
    LocalDateTime createdAt
) {}