package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "borrow_details")
@Getter
@Setter
@NoArgsConstructor
public class BorrowDetailsJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "borrow_slip_id", nullable = false)
    private Long borrowSlipId;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "returned_by_user_id")
    private Long returnedByUserId;

    @DecimalMin(value = "0.00", message = "Fine amount must be greater than or equal to 0")
    @Column(name = "fine_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal fineAmount = BigDecimal.ZERO;

    @Column(name = "fine_reason", length = 255)
    private String fineReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BorrowDetailsJpaEntity(Long id, Long borrowSlipId, Long bookId, LocalDateTime returnedAt,
                                  Long returnedByUserId, BigDecimal fineAmount, String fineReason,
                                  LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.borrowSlipId = borrowSlipId;
        this.bookId = bookId;
        this.returnedAt = returnedAt;
        this.returnedByUserId = returnedByUserId;
        this.fineAmount = fineAmount != null ? fineAmount : BigDecimal.ZERO;
        this.fineReason = fineReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        validateAndNormalizeFineAmount();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
        validateAndNormalizeFineAmount();
    }

    private void validateAndNormalizeFineAmount() {
        if (fineAmount == null) {
            fineAmount = BigDecimal.ZERO;
        } else if (fineAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fine amount cannot be negative");
        }
    }
}