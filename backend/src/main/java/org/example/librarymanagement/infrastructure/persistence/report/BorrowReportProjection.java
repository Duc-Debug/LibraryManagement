package org.example.librarymanagement.infrastructure.persistence.report;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BorrowReportProjection {
    Long getBorrowSlipId();
    String getBorrowCode();
    String getReaderCardCode();
    String getReaderName();
    String getBookTitles();
    Long getTotalBooks();
    LocalDateTime getBorrowedAt();
    LocalDateTime getDueAt();
    LocalDateTime getActualReturnedAt();
    String getStatus();
    BigDecimal getFineAmount();
    String getCreatedByUserName();
}
