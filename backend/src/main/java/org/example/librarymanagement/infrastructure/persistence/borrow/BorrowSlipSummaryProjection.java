package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.time.LocalDateTime;

public interface BorrowSlipSummaryProjection {
    Long getId();
    String getBorrowCode();
    Long getReaderId();
    String getReaderCardNumber();
    String getReaderName();
    Long getCreatedByUserId();
    String getCreatedByUserName();
    LocalDateTime getBorrowedAt();
    LocalDateTime getDueAt();
    String getStatus();
    String getNote();
    Integer getTotalBooks();
    LocalDateTime getCreatedAt();
}