package org.example.librarymanagement.application.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.example.librarymanagement.domain.entity.Readers;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.ReaderAlreadyExistsException;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderResult;
import org.example.librarymanagement.port.inbound.reader.CreateReaderUseCase;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.reader.CardNumberGeneratorPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReaderManagementService implements CreateReaderUseCase {

    private final ReaderRepositoryPort readerRepositoryPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;
    private final CardNumberGeneratorPort cardNumberGeneratorPort;

    @Override
    public CreateReaderResult createReader(CreateReaderCommand command) {
        if (readerRepositoryPort.existsByEmail(command.email())) {
            throw ReaderAlreadyExistsException.withEmail(command.email());
        }

        if (readerRepositoryPort.existsByPhoneNumber(command.phoneNumber())) {
            throw ReaderAlreadyExistsException.withPhoneNumber(command.phoneNumber());
        }

        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw org.example.librarymanagement.domain.exceptions.UnauthenticatedException.defaultMessage();
        }

        String cardNumber = cardNumberGeneratorPort.generateNextCardNumber();
        LocalDateTime now = LocalDateTime.now();
        LocalDate cardIssuedAt = LocalDate.now();
        LocalDate cardExpiryAt = cardIssuedAt.plusYears(1);

        Readers newReader = Readers.builder()
                .cardNumber(cardNumber)
                .name(command.name())
                .email(command.email())
                .phoneNumber(command.phoneNumber())
                .address(command.address())
                .cardStatus(CardStatus.ACTIVE)
                .cardIssuedAt(cardIssuedAt)
                .cardExpiryAt(cardExpiryAt)
                .createdAt(now)
                .updatedAt(now)
                .isActive(true)
                .build();

        Readers savedReader = readerRepositoryPort.save(newReader);
        return mapToResult(savedReader);
    }

    @Override
    public java.util.List<CreateReaderResult> getAllReaders() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw org.example.librarymanagement.domain.exceptions.UnauthenticatedException.defaultMessage();
        }

        return readerRepositoryPort.findAll().stream()
                .map(this::mapToResult)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public org.example.librarymanagement.port.dtos.common.PageResult<CreateReaderResult> getAllReaders(int page, int size) {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw org.example.librarymanagement.domain.exceptions.UnauthenticatedException.defaultMessage();
        }

        org.example.librarymanagement.port.dtos.common.PageResult<Readers> domainPage =
                readerRepositoryPort.findAll(page, size);

        java.util.List<CreateReaderResult> content = domainPage.content().stream()
                .map(this::mapToResult)
                .collect(java.util.stream.Collectors.toList());

        return org.example.librarymanagement.port.dtos.common.PageResult.of(
                content,
                domainPage.page(),
                domainPage.size(),
                domainPage.totalElements()
        );
    }

    private CreateReaderResult mapToResult(Readers r) {
        return new CreateReaderResult(
                r.getId(),
                r.getCardNumber(),
                r.getName(),
                r.getEmail(),
                r.getPhoneNumber(),
                r.getAddress(),
                r.getCardStatus(),
                r.getCardIssuedAt(),
                r.getCardExpiryAt(),
                "H\u1ec7 th\u1ed1ng"
        );
    }
}
