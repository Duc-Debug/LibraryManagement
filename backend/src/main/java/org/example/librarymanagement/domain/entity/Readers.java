package org.example.librarymanagement.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;

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

    public Readers(Builder builder) {
        validate(builder);

        this.id = builder.id;
        this.cardNumber = builder.cardNumber;
        this.name = builder.name;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.address = builder.address;
        this.cardStatus = builder.cardStatus;
        this.cardIssuedAt = builder.cardIssuedAt;
        this.cardExpiryAt = builder.cardExpiryAt;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
        this.isActive = builder.isActive;
        this.createdByUserId = builder.createdByUserId;
    }

    private static void validate(Builder builder) {
        validateNotBlank(builder.name, "Họ tên không được để trống hoặc chỉ chứa khoảng trắng");
        validateNotBlank(builder.email, "Email không được để trống hoặc chỉ chứa khoảng trắng");
        validateNotBlank(builder.phoneNumber, "Số điện thoại không được để trống hoặc chỉ chứa khoảng trắng");
        validateNotBlank(builder.address, "Địa chỉ không được để trống hoặc chỉ chứa khoảng trắng");
        validateNotBlank(builder.cardNumber, "Mã thẻ không được để trống hoặc chỉ chứa khoảng trắng");

        if (builder.cardStatus == null) {
            throw new DomainException("Trạng thái thẻ không được để null");
        }
    }


    // Tái cấu trúc Constructor cũ để tương thích (tùy chọn)
    public Readers(Long id, String cardNumber, String name, String email, String phoneNumber, String address, CardStatus cardStatus, LocalDate cardIssuedAt, LocalDate cardExpiryAt, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isActive, Long createdByUserId) {
        this(builder()
                .id(id)
                .cardNumber(cardNumber)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .address(address)
                .cardStatus(cardStatus)
                .cardIssuedAt(cardIssuedAt)
                .cardExpiryAt(cardExpiryAt)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .isActive(isActive)
                .createdByUserId(createdByUserId));
    }
public void updateInformation(
        String name,
        String email,
        String phoneNumber,
        String address,
        LocalDateTime updatedAt
) {
    validateNotBlank(
            name,
            "Reader name must not be blank."
    );

    validateNotBlank(
            email,
            "Email must not be blank."
    );

    validateNotBlank(
            phoneNumber,
            "Phone number must not be blank."
    );

    validateNotBlank(
            address,
            "Address must not be blank."
    );

    if (updatedAt == null) {
        throw new DomainException(
                "Updated time must not be null."
        );
    }

    if (!isActive) {
        throw new DomainException(
                "Cannot update an inactive reader."
        );
    }

    this.name = normalize(name);
    this.email = normalizeEmail(email);
    this.phoneNumber = normalize(phoneNumber);
    this.address = normalize(address);
    this.updatedAt = updatedAt;
}
    public static Builder builder() {
        return new Builder();
    }

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
        private boolean isActive = true;
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

        public Builder isActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder createdByUserId(Long createdByUserId) {
            this.createdByUserId = createdByUserId;
            return this;
        }

        public Readers build() {
            return new Readers(this);
        }
    }
    public void deactivate(LocalDateTime updatedAt) {
    if (updatedAt == null) {
        throw new DomainException(
                "Updated time must not be null."
        );
    }

    if (!isActive) {
        throw new DomainException(
                "Reader is already inactive."
        );
    }

    this.isActive = false;
    this.updatedAt = updatedAt;
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
    private static void validateNotBlank(
        String value,
        String message
) {
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

public void changeCardStatus(CardStatus newStatus, LocalDateTime updatedAt) {
    if (newStatus == null) {
        throw new DomainException("Trạng thái thẻ mới không được null");
    }
    if (updatedAt == null) {
        throw new DomainException("Thời gian cập nhật không được null");
    }
    if (!isActive) {
        throw new DomainException("Không thể cập nhật trạng thái thẻ của độc giả đã bị xóa");
    }
    this.cardStatus = newStatus;
    this.updatedAt = updatedAt;
}
}
