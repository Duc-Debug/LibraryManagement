package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class BorrowDetails {
    private Long id;
    private Long borrowSlipId;
    private Long bookId;
    private LocalDateTime returnAt;
    private Long returnByUserId;
    private String fineReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BorrowDetails create(Long borrowSlipId, Long bookId) {
        LocalDateTime now = LocalDateTime.now();
        return new BorrowDetails(null, borrowSlipId, bookId, null, null, null, now, now);
    }

    public BorrowDetails(Long id, Long borrowSlipId, Long bookId, LocalDateTime returnAt, Long returnByUserId,
            String fineReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        validateRequiredIds(borrowSlipId, bookId);
        validateReturnInformation(returnAt, returnByUserId, fineReason);
        this.id = id;
        this.borrowSlipId = borrowSlipId;
        this.bookId = bookId;
        this.returnAt = returnAt;
        this.returnByUserId = returnByUserId;
        this.fineReason = normalizeNullable(fineReason);
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? createdAt : LocalDateTime.now();
    }

    // ==================== DOMAIN BUSINESS BEHAVIORS ====================
    /**
     * Hành vi nghiệp vụ: Thực hiện Trả Sách
     */

    // ==================== HELPER VALIDATIONS ====================
    private static void validateRequiredIds(Long borrowSlipId, Long bookId) {
        if (borrowSlipId == null) {
            throw new DomainException("Borrow slip ID must not be null");
        }
        if (bookId == null) {
            throw new DomainException("Book ID must not be null");
        }
    }

    private static void validateReturnInformation(
            LocalDateTime returnAt,
            Long returnByUserId,
            String fineReason) {
        if (returnAt != null && returnByUserId == null) {
            throw new DomainException("Return by user ID must not be null when return date is set");
        }
        if (fineReason != null && fineReason.trim().length() > 255) {
            throw new DomainException("Fine reason cannot exceed 255 characters");
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    private static String normalizeNullable(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    // ==================== GETTERS ONLY (NO PUBLIC SETTERS) ====================
    public Long getId() {
        return id;
    }

    public Long getBorrowSlipId() {
        return borrowSlipId;
    }

    public Long getBookId() {
        return bookId;
    }

    public LocalDateTime getReturnAt() {
        return returnAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Long getReturnByUserId() {
        return returnByUserId;
    }

    public String getFineReason() {
        return fineReason;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BorrowDetails details = (BorrowDetails) o;
        return Objects.equals(id, details.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
