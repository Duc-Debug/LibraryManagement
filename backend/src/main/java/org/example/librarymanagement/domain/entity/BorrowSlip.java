package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class BorrowSlip {

    private Long id;
    private Long readerId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BorrowSlipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BorrowSlip create(Long readerId, int borrowDays, LocalDateTime borrowDate) {
        if (readerId == null || readerId <= 0) {
            throw new DomainException("Reader ID must be greater than 0");
        }
        if (borrowDays <= 0) {
            throw new DomainException("Borrow days must be greater than 0");
        }
        if (borrowDate == null) {
            throw new DomainException("Borrow date must not be null");
        }
        LocalDateTime calculatedDueDate = borrowDate.plusDays(borrowDays);
        return new BorrowSlip(
                null,
                readerId,
                borrowDate,
                calculatedDueDate,
                null,
                BorrowSlipStatus.BORROWING,
                borrowDate,
                borrowDate);
    }

    public static BorrowSlip create(Long readerId, int borrowDays) {
        return create(readerId, borrowDays, LocalDateTime.now());
    }

    public BorrowSlip(
            Long id,
            Long readerId,
            LocalDateTime borrowDate,
            LocalDateTime dueDate,
            LocalDateTime returnDate,
            BorrowSlipStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        validateRequiredIds(readerId);
        validateDates(borrowDate, dueDate, returnDate);
        this.id = id;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public BorrowSlip(
            Long id,
            Long readerId,
            LocalDateTime borrowDate,
            LocalDateTime dueDate,
            BorrowSlipStatus status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this(id, readerId, borrowDate, dueDate, null, status, createdAt, updatedAt);
    }

    // ==================== DOMAIN BUSINESS BEHAVIORS ====================
    public void markReturned(LocalDateTime actualReturnDate) {
        if (actualReturnDate != null && borrowDate != null && actualReturnDate.isBefore(borrowDate)) {
            throw new DomainException("Return date cannot be before borrow date");
        }
        this.status = BorrowSlipStatus.RETURNED;
        this.returnDate = actualReturnDate != null ? actualReturnDate : LocalDateTime.now();
        touch();
    }

    public void markReturned() {
        markReturned(LocalDateTime.now());
    }

    public void markOverdue() {
        this.status = BorrowSlipStatus.OVERDUE;
        touch();
    }

    // ==================== HELPER VALIDATIONS ====================
    private static void validateRequiredIds(Long readerId) {
        if (readerId == null || readerId <= 0) {
            throw new DomainException("Reader ID must be greater than 0");
        }
    }

    private static void validateDates(LocalDateTime borrowDate, LocalDateTime dueDate, LocalDateTime returnDate) {
        if (borrowDate != null && dueDate != null && dueDate.isBefore(borrowDate)) {
            throw new DomainException("Due date cannot be before borrow date");
        }
        if (borrowDate != null && returnDate != null && returnDate.isBefore(borrowDate)) {
            throw new DomainException("Return date cannot be before borrow date");
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== GETTERS ONLY (NO PUBLIC SETTERS) ====================
    public Long getId() {
        return id;
    }

    public Long getReaderId() {
        return readerId;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public BorrowSlipStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
        BorrowSlip that = (BorrowSlip) o;
        return id != null && that.id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "BorrowSlip{"
                + "id=" + id
                + ", readerId=" + readerId
                + ", borrowDate=" + borrowDate
                + ", dueDate=" + dueDate
                + ", returnDate=" + returnDate
                + ", status=" + status
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
