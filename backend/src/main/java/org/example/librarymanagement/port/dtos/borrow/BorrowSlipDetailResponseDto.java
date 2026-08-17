package org.example.librarymanagement.port.dtos.borrow;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;

public record BorrowSlipDetailResponseDto(
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
    LocalDateTime createdAt,
    List<BorrowSlipItemResponseDto> items
) {}
