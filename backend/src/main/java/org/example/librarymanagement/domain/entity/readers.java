package org.example.librarymanagement.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.example.librarymanagement.domain.enums.card_status;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class readers {

    private UUID id;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private card_status cardStatus;
    private LocalDate card_issued_at;
    private LocalDate card_expiry_at;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;

    public readers(UUID id, String name, String email, String phoneNumber, String address, card_status cardStatus, LocalDate card_issued_at, LocalDate card_expiry_at, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive) {
        if (name == null || name.isEmpty()) {
            throw new DomainException("Name cannot be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new DomainException("Email cannot be null or empty");
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new DomainException("Phone number cannot be null or empty");
        }
        if (address == null || address.isEmpty()) {
            throw new DomainException("Address cannot be null or empty");
        }
        if (cardStatus == null) {
            throw new DomainException("Card status cannot be null");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.cardStatus = cardStatus;
        this.card_issued_at = card_issued_at;
        this.card_expiry_at = card_expiry_at;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public card_status getCardStatus() {
        return cardStatus;
    }

    public LocalDate getCardIssuedAt() {
        return card_issued_at;
    }

    public LocalDate getCardExpiryAt() {
        return card_expiry_at;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return isActive;
    }

}
