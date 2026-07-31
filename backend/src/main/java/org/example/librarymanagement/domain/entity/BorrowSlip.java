package org.example.librarymanagement.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
@Builder
@NoArgsConstructor
public class BorrowSlip {

    private Long id;
    private Long readerId;
    private LocalDateTime borrowDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BorrowSlipStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 1. Constructor dùng khi tạo phiếu mượn mới (Tạo mượn sách)
    public BorrowSlip(Long readerId, LocalDateTime dueDate) {
        if (readerId == null || readerId <= 0) {
            throw new DomainException("Reader ID must be greater than 0.");
        }
        
        LocalDateTime now = LocalDateTime.now();
        if (dueDate == null || dueDate.isBefore(now)) {
            throw new DomainException("Due date must be after borrow date.");
        }

        this.readerId = readerId;
        this.borrowDate = now;
        this.dueDate = dueDate;
        this.status = BorrowSlipStatus.BORROWING;
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 2. Constructor Re-constitute từ DB (Validation trực tiếp, không dùng Setter)
    public BorrowSlip(Long id, Long readerId, LocalDateTime borrowDate, LocalDateTime dueDate,
                      LocalDateTime returnDate, BorrowSlipStatus status, 
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        
        if (id != null && id <= 0) {
            throw new DomainException("Id must be greater than 0.");
        }
        if (readerId == null || readerId <= 0) {
            throw new DomainException("Reader ID must be greater than 0.");
        }
        if (borrowDate == null) {
            throw new DomainException("Borrow date cannot be null.");
        }
        if (dueDate == null || dueDate.isBefore(borrowDate)) {
            throw new DomainException("Due date must be after borrow date.");
        }
        if (returnDate != null && returnDate.isBefore(borrowDate)) {
            throw new DomainException("Return date cannot be before borrow date.");
        }

        this.id = id;
        this.readerId = readerId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = Objects.requireNonNull(status, "Status cannot be null.");
        this.createdAt = Objects.requireNonNull(createdAt, "Created time cannot be null.");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Updated time cannot be null.");
    }

    // --- DOMAIN BUSINESS METHODS (Setter) ---

    // Hành động Trả sách
    public void markAsReturned() {
        if (this.status == BorrowSlipStatus.RETURNED) {
            throw new DomainException("Book has already been returned.");
        }
        this.returnDate = LocalDateTime.now();
        this.status = BorrowSlipStatus.RETURNED;
        this.updatedAt = LocalDateTime.now();
    }

    // Kiểm tra xem phiếu mượn đã quá hạn chưa
    public boolean isOverdue() {
        if (status == BorrowSlipStatus.RETURNED) {
            return false;
        }
        return LocalDateTime.now().isAfter(dueDate);
    }
}