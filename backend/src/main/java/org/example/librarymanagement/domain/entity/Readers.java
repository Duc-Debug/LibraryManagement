package org.example.librarymanagement.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class Readers {

    private Long id;
    private String cardNumber;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;
    private CardStatus cardStatus;
    private LocalDate cardIssuedAt;
    private LocalDate cardExpiryAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isActive;
    private Long createdByUserId;

    public Readers(Long id, String cardNumber, String name, String email, String phoneNumber, String address, CardStatus cardStatus, LocalDate cardIssuedAt, LocalDate cardExpiryAt, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive) {
        this(id, cardNumber, name, email, phoneNumber, address, cardStatus, cardIssuedAt, cardExpiryAt, createdAt, updatedAt, isActive, null);
    }

    public Readers(Long id, String cardNumber, String name, String email, String phoneNumber, String address, CardStatus cardStatus, LocalDate cardIssuedAt, LocalDate cardExpiryAt, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive, Long createdByUserId) {
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
        if (cardNumber == null) {
            throw new DomainException("Card number cannot be null");
        }

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

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public Long getId() {
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

    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public LocalDate getCardIssuedAt() {
        return cardIssuedAt;
    }

    public LocalDate getCardExpiryAt() {
        return cardExpiryAt;
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
