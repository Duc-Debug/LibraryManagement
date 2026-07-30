package org.example.librarymanagement.domain.borrowing;
import java.time.LocalDateTime;
import java.util.UUID;

import org.example.librarymanagement.domain.shared.exceptions.DomainException;
public class BorrowDetails {
    private UUID id;
    private UUID borrowSlipId;
    private UUID bookId;
    private LocalDateTime returnAt;
    private UUID returnByUserId;
    private String fineReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BorrowDetails() {}

    public BorrowDetails(UUID id, UUID borrowSlipId, UUID bookId, LocalDateTime returnAt, UUID returnByUserId, String fineReason, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.borrowSlipId = borrowSlipId;
        this.bookId = bookId;
        this.returnAt = returnAt;
        this.returnByUserId = returnByUserId;
        this.fineReason = fineReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        if(id == null) {
            throw new DomainException("ID cannot be null");
        }
      this.id = id;
    }
    
    public UUID getBorrowSlipId() {
        return borrowSlipId;
    }
    public void setBorrowSlipId(UUID borrowSlipId) {
        if(borrowSlipId == null) {
            throw new DomainException("Borrow slip ID cannot be null");
        }
        this.borrowSlipId = borrowSlipId;
    }

    public UUID getBookId() {
        return bookId;
    }
    public void setBookId(UUID bookId) {
        if(bookId == null) {
            throw new DomainException("Book ID cannot be null");
        }
        this.bookId = bookId;
    }

    public LocalDateTime getReturnAt() {
        return returnAt;
    }
    public void setReturnAt(LocalDateTime returnAt) {
        if( createdAt != null && returnAt != null && returnAt.isBefore(createdAt)) {
            throw new DomainException("Return date cannot be before created date");
        }
        this.returnAt = returnAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        if(createdAt == null) {
            throw new DomainException("Created date cannot be null");
        }
        this.createdAt = createdAt;
    }

    public UUID getReturnByUserId() {
        return returnByUserId;
    }
    public void setReturnByUserId(UUID returnByUserId) {
        if(returnAt != null && returnByUserId == null) {
            throw new DomainException("Return by user ID cannot be null when return date is set");
        }
        this.returnByUserId = returnByUserId;
    }

    public String getFineReason() {
        return fineReason;
    }
    public void setFineReason(String fineReason) {
        if(fineReason == null) {
            throw new DomainException("Fine reason cannot be null");
        }
        if(fineReason.length() > 255) {
            throw new DomainException("Fine reason cannot exceed 255 characters");
        }
        if(fineReason.trim().isEmpty()) {
            throw new DomainException("Fine reason cannot be empty");
        }
        this.fineReason = fineReason;


   
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        if(updatedAt != null && createdAt != null && updatedAt.isBefore(createdAt)) {
            throw new DomainException("Updated date cannot be before created date");
        }
        this.updatedAt = updatedAt;
    }




    
}
