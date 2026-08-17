package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface BorrowDetailItemProjection {
    Long getId();
    Long getBookId();
    String getBookTitle();
    String getIsbn();
    String getAuthor();
    String getCategoryName();
    String getCoverUrl();
    LocalDateTime getReturnedAt();
    BigDecimal getFineAmount();
    String getFineReason();
}
