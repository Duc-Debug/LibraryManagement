package org.example.librarymanagement.infrastructure.persistence.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.librarymanagement.domain.enums.CardStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "readers",
    indexes = {
        @Index(name = "idx_reader_card_number", columnList = "card_number"),
        @Index(name = "idx_reader_email", columnList = "email"),
        @Index(name = "idx_reader_phone", columnList = "phone_number")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ReaderJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "card_number", nullable = false, unique = true, length = 50)
    private String cardNumber;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_status", nullable = false, length = 20)
    private CardStatus cardStatus;

    @Column(name = "card_issued_at", nullable = false)
    private LocalDate cardIssuedAt;

    @Column(name = "card_expiry_at", nullable = false)
    private LocalDate cardExpiryAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    public ReaderJpaEntity(Long id, String cardNumber, String name, String email, String phoneNumber, String address,
            CardStatus cardStatus, LocalDate cardIssuedAt, LocalDate cardExpiryAt,
            LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive) {
        this(id, cardNumber, name, email, phoneNumber, address, cardStatus, cardIssuedAt, cardExpiryAt, createdAt, updatedAt, isActive, null);
    }

    public ReaderJpaEntity(Long id, String cardNumber, String name, String email, String phoneNumber, String address,
            CardStatus cardStatus, LocalDate cardIssuedAt, LocalDate cardExpiryAt,
            LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive, Long createdByUserId) {
        this.id = id;
        this.cardNumber = cardNumber;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.cardStatus = cardStatus;
        this.cardIssuedAt = cardIssuedAt;
        this.cardExpiryAt = cardExpiryAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
        this.createdByUserId = createdByUserId;
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
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
