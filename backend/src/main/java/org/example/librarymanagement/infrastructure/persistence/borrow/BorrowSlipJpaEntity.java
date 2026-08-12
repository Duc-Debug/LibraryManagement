package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "borrow_slips")
@Getter
@Setter
@NoArgsConstructor
public class BorrowSlipJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "borrow_code", nullable = false, unique = true, length = 30)
    private String borrowCode;

    @Column(name = "reader_id", nullable = false)
    private Long readerId;

    @Column(name = "created_by_user_id", nullable = false)
    private Long createdByUserId;

    @Column(name = "borrowed_at", nullable = false)
    private LocalDateTime borrowedAt;

    @Column(name = "due_at", nullable = false)
    private LocalDateTime dueAt;
    @Column(name = "return_date")
private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private BorrowSlipStatus status;

    @Column(name = "note", length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BorrowSlipJpaEntity(Long id, String borrowCode, Long readerId, Long createdByUserId,
                               LocalDateTime borrowedAt, LocalDateTime dueAt, LocalDateTime returnDate, BorrowSlipStatus status,
                               String note, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.borrowCode = borrowCode;
        this.readerId = readerId;
        this.createdByUserId = createdByUserId;
        this.borrowedAt = borrowedAt;
        this.dueAt = dueAt;
            this.returnDate = returnDate;
        this.status = status;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
   public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}