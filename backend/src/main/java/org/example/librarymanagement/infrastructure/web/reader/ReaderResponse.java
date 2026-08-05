package org.example.librarymanagement.infrastructure.web.reader;

import java.time.LocalDate;
import java.util.UUID;

import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.port.dtos.reader.CreateReaderResult;

public record ReaderResponse(
        Long id,
        String cardNumber,
        String name,
        String email,
        String phoneNumber,
        String address,
        CardStatus cardStatus,
        LocalDate cardIssuedAt,
        LocalDate cardExpiryAt
) {
    public static ReaderResponse fromResult(CreateReaderResult result) {
        return new ReaderResponse(
                result.id(),
                result.cardNumber(),
                result.name(),
                result.email(),
                result.phoneNumber(),
                result.address(),
                result.cardStatus(),
                result.cardIssuedAt(),
                result.cardExpiryAt()
        );
    }
}
