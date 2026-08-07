package org.example.librarymanagement.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class Reader {

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
    private boolean active;
    private Long createdByUserId;

    // Constructor nhận Builder
    private Reader(Builder builder) {
        validateBuilder(builder);

        this.id = builder.id;
        this.cardNumber = builder.cardNumber.trim();
        this.name = normalize(builder.name);
        this.email = normalizeEmail(builder.email);
        this.phoneNumber = normalize(builder.phoneNumber);
        this.address = normalize(builder.address);
        this.cardStatus = builder.cardStatus;
        this.cardIssuedAt = builder.cardIssuedAt != null ? builder.cardIssuedAt : LocalDate.now();
        this.cardExpiryAt = builder.cardExpiryAt != null ? builder.cardExpiryAt : LocalDate.now().plusYears(1);
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : LocalDateTime.now();
        this.active = builder.active;
        this.createdByUserId = builder.createdByUserId;
    }

    // Static Builder Creator
    public static Builder builder() {
        return new Builder();
    }

    // ==================== DOMAIN BUSINESS BEHAVIORS ====================

    /**
     * Cập nhật thông tin cá nhân của độc giả
     */
    public void updateInformation(String name, String email, String phoneNumber, String address) {
        if (!active) {
            throw new DomainException("Cannot update an inactive reader");
        }

        validateNotBlank(name, "Reader name must not be blank");
        validateNotBlank(email, "Email must not be blank");
        validateNotBlank(phoneNumber, "Phone number must not be blank");
        validateNotBlank(address, "Address must not be blank");

        this.name = normalize(name);
        this.email = normalizeEmail(email);
        this.phoneNumber = normalize(phoneNumber);
        this.address = normalize(address);
        touch();
    }

    /**
     * Thay đổi trạng thái thẻ độc giả
     */
    public void changeCardStatus(CardStatus newStatus) {
        if (newStatus == null) {
            throw new DomainException("New card status must not be null");
        }
        if (!active) {
            throw new DomainException("Cannot update card status of a deactivated reader");
        }
        this.cardStatus = newStatus;
        touch();
    }

    /**
     * Vô hiệu hóa tài khoản độc giả
     */
    public void deactivate() {
        if (!active) {
            throw new DomainException("Reader is already inactive");
        }
        this.active = false;
        touch();
    }

    /**
     * Kích hoạt lại tài khoản độc giả
     */
    public void activate() {
        if (active) {
            throw new DomainException("Reader is already active");
        }
        this.active = true;
        touch();
    }

    // ==================== HELPER VALIDATIONS & UTILS ====================

    private static void validateBuilder(Builder builder) {
        validateNotBlank(builder.name, "Reader name must not be blank");
        validateNotBlank(builder.email, "Email must not be blank");
        validateNotBlank(builder.phoneNumber, "Phone number must not be blank");
        validateNotBlank(builder.address, "Address must not be blank");
        validateNotBlank(builder.cardNumber, "Card number must not be blank");

        if (builder.cardStatus == null) {
            throw new DomainException("Card status must not be null");
        }
    }

    private static void validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new DomainException(message);
        }
    }

    private static String normalize(String value) {
        return value.trim();
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    // ==================== BUILDER CLASS ====================

    public static class Builder {
        private Long id;
        private String cardNumber;
        private String name;
        private String email;
        private String phoneNumber;
        private String address;
        private CardStatus cardStatus = CardStatus.ACTIVE;
        private LocalDate cardIssuedAt;
        private LocalDate cardExpiryAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private boolean active = true;
        private Long createdByUserId;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder cardStatus(CardStatus cardStatus) {
            this.cardStatus = cardStatus;
            return this;
        }

        public Builder cardIssuedAt(LocalDate cardIssuedAt) {
            this.cardIssuedAt = cardIssuedAt;
            return this;
        }

        public Builder cardExpiryAt(LocalDate cardExpiryAt) {
            this.cardExpiryAt = cardExpiryAt;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder isActive(boolean active) {
            this.active = active;
            return this;
        }

        public Builder createdByUserId(Long createdByUserId) {
            this.createdByUserId = createdByUserId;
            return this;
        }

        public Reader build() {
            return new Reader(this);
        }
    }

    // ==================== GETTERS ONLY (NO PUBLIC SETTERS) ====================

    public Long getId() {
        return id;
    }

    public String getCardNumber() {
        return cardNumber;
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
        return active;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reader reader = (Reader) o;
        return Objects.equals(id, reader.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}