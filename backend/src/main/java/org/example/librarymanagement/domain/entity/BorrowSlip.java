package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.enums.BorrowSlip_Status;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class BorrowSlip {

    private Long id;
    private Long readerId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BorrowSlip_Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BorrowSlip() {
    }

    public BorrowSlip(Long id,
            Long readerId,
            LocalDateTime borrowDate,
            LocalDateTime dueDate,
            LocalDateTime returnDate,
            BorrowSlip_Status status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        setId(id);
        setReaderId(readerId);
        setBorrowDate(borrowDate);
        setDueDate(dueDate);
        setReturnDate(returnDate);
        setStatus(status);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id <= 0) {
            throw new DomainException("Id must be greater than 0.");
        }
        this.id = id;
    }

    public Long getReaderId() {
        return readerId;
    }

    public void setReaderId(Long readerId) {
        if (readerId == null || readerId <= 0) {
            throw new DomainException("Reader ID must be greater than 0.");
        }
        this.readerId = readerId;
    }

    public LocalDateTime getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDateTime borrowDate) {
        if (borrowDate == null) {
            throw new DomainException("Borrow date cannot be null.");
        }
        this.borrowDate = borrowDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        if (dueDate == null) {
            throw new DomainException("Due date cannot be null.");
        }

        if (borrowDate != null && dueDate.isBefore(borrowDate)) {
            throw new DomainException("Due date must be after borrow date.");
        }

        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {

        if (returnDate != null
                && borrowDate != null
                && returnDate.isBefore(borrowDate)) {

            throw new DomainException("Return date cannot be before borrow date.");
        }

        this.returnDate = returnDate;
    }

    public BorrowSlip_Status getStatus() {
        return status;
    }

    public void setStatus(BorrowSlip_Status status) {
        if (status == null) {
            throw new DomainException("Status cannot be null.");
        }
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new DomainException("Created time cannot be null.");
        }
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        if (updatedAt == null) {
            throw new DomainException("Updated time cannot be null.");
        }
        this.updatedAt = updatedAt;
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
