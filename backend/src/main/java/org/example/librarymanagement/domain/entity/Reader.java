package org.example.librarymanagement.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reader {

    private UUID id;
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

    public Reader(String name, String email, String phoneNumber, String address, CardStatus cardStatus, LocalDate cardIssuedAt, LocalDate cardExpiryAt) {
        this.name = requireNotBlank(name, "Name cannot be null or empty");
        this.email = requireNotBlank(email, "Email cannot be null or empty");
        this.phoneNumber = requireNotBlank(phoneNumber, "Phone number cannot be null or empty");
        this.address = requireNotBlank(address, "Address cannot be null or empty");
        this.cardStatus = Objects.requireNonNull(cardStatus, "Card status cannot be null");
        
        this.cardIssuedAt = cardIssuedAt;
        this.cardExpiryAt = cardExpiryAt;
        this.active = true;

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    private static String requireNotBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new DomainException(errorMessage);
        }
        return value.trim();
    }
}