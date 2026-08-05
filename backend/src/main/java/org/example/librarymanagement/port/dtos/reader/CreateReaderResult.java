package org.example.librarymanagement.port.dtos.reader;

import java.time.LocalDate;
import java.util.UUID;

import org.example.librarymanagement.domain.enums.CardStatus;

public record CreateReaderResult(
        Long id,
        String cardNumber,
        String name,
        String email,
        String phoneNumber,
        String address,
        CardStatus cardStatus,
        LocalDate cardIssuedAt,
        LocalDate cardExpiryAt,
        String createdByName
        ) {

}
