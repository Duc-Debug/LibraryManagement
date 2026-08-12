package org.example.librarymanagement.infrastructure.persistence.report;

import java.math.BigDecimal;

public interface BorrowReportSummaryAggregationProjection {
    Long getTotalSlips();
    Long getTotalBooksBorrowed();
    Long getTotalReturned();
    Long getTotalOverdue();
    Long getTotalBorrowing();
    BigDecimal getTotalFineAmount();
}
