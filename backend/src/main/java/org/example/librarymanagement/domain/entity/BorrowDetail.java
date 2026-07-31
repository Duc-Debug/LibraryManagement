package org.example.librarymanagement.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@Builder
@NoArgsConstructor
public class BorrowDetail {

    private UUID id;
    private UUID borrowSlipId;
    private UUID bookId;
    private LocalDateTime returnAt;
    private UUID returnByUserId;
    private String fineReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 1. Constructor dùng khi tạo chi tiết mượn mới
    public BorrowDetail(UUID borrowSlipId, UUID bookId) {
        if (borrowSlipId == null) {
            throw new DomainException("Borrow slip ID cannot be null");
        }
        if (bookId == null) {
            throw new DomainException("Book ID cannot be null");
        }

        LocalDateTime now = LocalDateTime.now();
        this.borrowSlipId = borrowSlipId;
        this.bookId = bookId;
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 2. Constructor dùng khi Re-constitute từ DB
    public BorrowDetail(UUID id, UUID borrowSlipId, UUID bookId, LocalDateTime returnAt,
                        UUID returnByUserId, String fineReason, LocalDateTime createdAt, LocalDateTime updatedAt) {

        if (id == null) {
            throw new DomainException("ID cannot be null");
        }
        if (borrowSlipId == null) {
            throw new DomainException("Borrow slip ID cannot be null");
        }
        if (bookId == null) {
            throw new DomainException("Book ID cannot be null");
        }
        if (createdAt == null) {
            throw new DomainException("Created date cannot be null");
        }
        if (returnAt != null && returnAt.isBefore(createdAt)) {
            throw new DomainException("Return date cannot be before created date");
        }
        if (updatedAt != null && updatedAt.isBefore(createdAt)) {
            throw new DomainException("Updated date cannot be before created date");
        }
        if (returnAt != null && returnByUserId == null) {
            throw new DomainException("Return by user ID cannot be null when return date is set");
        }

        this.id = id;
        this.borrowSlipId = borrowSlipId;
        this.bookId = bookId;
        this.returnAt = returnAt;
        this.returnByUserId = returnByUserId;
        this.fineReason = validateFineReason(fineReason);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- DOMAIN BUSINESS METHODS ---

    // Xử lý nghiệp vụ trả sách cho từng đầu sách
    public void markAsReturned(UUID staffUserId, String fineReason) {
        if (staffUserId == null) {
            throw new DomainException("Return by user ID cannot be null when returning a book");
        }
        if (this.returnAt != null) {
            throw new DomainException("This item has already been returned");
        }

        LocalDateTime now = LocalDateTime.now();
        this.returnAt = now;
        this.returnByUserId = staffUserId;
        this.fineReason = validateFineReason(fineReason);
        this.updatedAt = now;
    }

    private String validateFineReason(String reason) {
        if (reason == null) {
            return null;
        }
        if (reason.isBlank()) {
            throw new DomainException("Fine reason cannot be empty or blank");
        }
        if (reason.length() > 255) {
            throw new DomainException("Fine reason cannot exceed 255 characters");
        }
        return reason.trim();
    }
}