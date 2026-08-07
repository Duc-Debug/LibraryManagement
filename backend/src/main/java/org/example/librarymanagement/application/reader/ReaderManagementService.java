package org.example.librarymanagement.application.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Readers;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.ReaderAccessDeniedException;
import org.example.librarymanagement.domain.exceptions.ReaderAlreadyExistsException;
import org.example.librarymanagement.domain.exceptions.ReaderHasActiveBorrowException;
import org.example.librarymanagement.domain.exceptions.ReaderNotFoundException;
import org.example.librarymanagement.domain.exceptions.UnauthenticatedException;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.dtos.reader.UpdateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ChangeCardStatusCommand;
import org.example.librarymanagement.port.inbound.reader.ReaderManagementUseCase;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveReaderBorrowPort;
import org.example.librarymanagement.port.outbound.manage.FindUserPort;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.reader.CardNumberGeneratorPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.reader.ReaderSearchCriteria;


public class ReaderManagementService implements ReaderManagementUseCase {

    private final ReaderRepositoryPort readerRepositoryPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;
    private final CardNumberGeneratorPort cardNumberGeneratorPort;
    private final FindUserPort findUserPort;
    private final CheckActiveReaderBorrowPort checkActiveReaderBorrowPort;
public ReaderManagementService(
        ReaderRepositoryPort readerRepositoryPort,
        GetAuthenticatedUserPort getAuthenticatedUserPort,
        CardNumberGeneratorPort cardNumberGeneratorPort,
        FindUserPort findUserPort,
        CheckActiveReaderBorrowPort checkActiveReaderBorrowPort
) {
    this.readerRepositoryPort = Objects.requireNonNull(
            readerRepositoryPort,
            "Reader repository port must not be null."
    );

    this.getAuthenticatedUserPort = Objects.requireNonNull(
            getAuthenticatedUserPort,
            "Authenticated user port must not be null."
    );

    this.cardNumberGeneratorPort = Objects.requireNonNull(
            cardNumberGeneratorPort,
            "Card number generator port must not be null."
    );

    this.findUserPort = Objects.requireNonNull(
            findUserPort,
            "Find user port must not be null."
    );

    this.checkActiveReaderBorrowPort = Objects.requireNonNull(
            checkActiveReaderBorrowPort,
            "Check active reader borrow port must not be null."
    );
}
    @Override
    public ReaderResult createReader(CreateReaderCommand command) {
        // 1. Kiểm tra Email đã tồn tại chưa
        if (readerRepositoryPort.existsByEmail(command.email())) {
            throw ReaderAlreadyExistsException.withEmail(command.email());
        }

        // 2. Kiểm tra Số điện thoại đã tồn tại chưa
        if (readerRepositoryPort.existsByPhoneNumber(command.phoneNumber())) {
            throw ReaderAlreadyExistsException.withPhoneNumber(command.phoneNumber());
        }

        // 3. Lấy thông tin Thủ thư đang đăng nhập
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw org.example.librarymanagement.domain.exceptions.UnauthenticatedException.defaultMessage();
        }
        Long creatorId = currentUser.getId();

        // 4. Sinh Mã thẻ qua Outbound Port (CardNumberGeneratorPort)
        String cardNumber = cardNumberGeneratorPort.generateNextCardNumber();

        // 5. Khởi tạo Domain Object Readers (Mặc định trạng thái ACTIVE, hạn 1 năm, lưu vết creatorId)
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
                .createdByUserId(creatorId)
                .build();

        // 6. Lưu thông tin bạn đọc xuống DB qua Outbound Port
        Readers savedReader = readerRepositoryPort.save(newReader);

        // 7. Chuyển đổi và trả về Result DTO
        return mapToResult(savedReader);
    }
@Override
public void deleteReader(Long readerId) {
    if (readerId == null || readerId <= 0) {
        throw new IllegalArgumentException(
                "Reader id must be greater than 0."
        );
    }

    User currentUser =
            getAuthenticatedUserPort.getCurrentUser();

    if (currentUser == null) {
        throw UnauthenticatedException.defaultMessage();
    }

    Readers reader = readerRepositoryPort
            .findById(readerId)
            .orElseThrow(() ->
                    ReaderNotFoundException.withId(readerId)
            );

    boolean admin = isAdmin(currentUser);

    boolean owner = currentUser.getId() != null
            && currentUser.getId().equals(
                    reader.getCreatedByUserId()
            );

    if (!admin && !owner) {
        throw ReaderAccessDeniedException.forReader(
                readerId
        );
    }
if (checkActiveReaderBorrowPort
        .hasActiveBorrowByReaderId(readerId)) {
        throw ReaderHasActiveBorrowException
                .withReaderId(readerId);
    }

    reader.deactivate(
            LocalDateTime.now()
    );

    readerRepositoryPort.save(reader);
}
@Override
public ReaderResult updateReader(
        UpdateReaderCommand command
) {
    if (command == null) {
        throw new IllegalArgumentException(
                "Update reader command must not be null."
        );
    }

    if (command.readerId() == null) {
        throw new IllegalArgumentException(
                "Reader id must not be null."
        );
    }

    User currentUser = getAuthenticatedUserPort.getCurrentUser();

    if (currentUser == null) {
        throw UnauthenticatedException.defaultMessage();
    }

    Readers reader = readerRepositoryPort
            .findById(command.readerId())
            .orElseThrow(() ->
                    ReaderNotFoundException.withId(
                            command.readerId()
                    )
            );

    boolean isAdmin = isAdmin(currentUser);
    boolean isOwner = currentUser.getId() != null
            && currentUser.getId().equals(
                    reader.getCreatedByUserId()
            );

    if (!isAdmin && !isOwner) {
        throw ReaderAccessDeniedException.forReader(
                reader.getId()
        );
    }

    String normalizedEmail =
            normalizeEmail(command.email());

    String normalizedPhoneNumber =
            normalizeRequired(
                    command.phoneNumber(),
                    "Phone number must not be blank."
            );

    if (readerRepositoryPort.existsByEmailAndIdNot(
            normalizedEmail,
            reader.getId()
    )) {
        throw ReaderAlreadyExistsException.withEmail(
                normalizedEmail
        );
    }

    if (readerRepositoryPort.existsByPhoneNumberAndIdNot(
            normalizedPhoneNumber,
            reader.getId()
    )) {
        throw ReaderAlreadyExistsException.withPhoneNumber(
                normalizedPhoneNumber
        );
    }

    reader.updateInformation(
            command.name(),
            normalizedEmail,
            normalizedPhoneNumber,
            command.address(),
            LocalDateTime.now()
    );

    Readers updatedReader =
            readerRepositoryPort.save(reader);

    return mapToResult(updatedReader);
}
@Override
public PageResult<ReaderResult> getAllReaders(
        int page,
        int size
) {
    User currentUser = requireCurrentUser();

    validatePagination(
            page,
            size
    );

    Long createdByUserId = scopedCreatedByUserId(currentUser);

    PageResult<Readers> domainPage =
            readerRepositoryPort.search(
                    new ReaderSearchCriteria(
                            null,
                            null,
                            createdByUserId,
                            page,
                            size
                    )
            );

    List<ReaderResult> content = domainPage.content().stream()
            .map(this::mapToResult)
            .collect(Collectors.toList());

    return PageResult.of(
            content,
            domainPage.page(),
            domainPage.size(),
            domainPage.totalElements()
    );
}

@Override
public ReaderResult changeCardStatus(ChangeCardStatusCommand command) {
    if (command == null) {
        throw new IllegalArgumentException("Command must not be null.");
    }
    if (command.readerId() == null || command.newStatus() == null) {
        throw new IllegalArgumentException("Reader ID and new status must not be null.");
    }

    User currentUser = requireCurrentUser();
    Readers reader = readerRepositoryPort.findById(command.readerId())
            .orElseThrow(() -> ReaderNotFoundException.withId(command.readerId()));

    boolean isAdmin = isAdmin(currentUser);
    boolean isOwner = currentUser.getId() != null
            && currentUser.getId().equals(reader.getCreatedByUserId());

    if (!isAdmin && !isOwner) {
        throw ReaderAccessDeniedException.forReader(reader.getId());
    }

    reader.changeCardStatus(command.newStatus(), LocalDateTime.now());
    Readers updatedReader = readerRepositoryPort.save(reader);

    return mapToResult(updatedReader);
}

private void validatePagination(
        int page,
        int size
) {
    if (page < 0) {
        throw new IllegalArgumentException(
                "Page must be greater than or equal to 0."
        );
    }

    if (size <= 0) {
        throw new IllegalArgumentException(
                "Size must be greater than 0."
        );
    }

    if (size > ReaderSearchCriteria.MAX_PAGE_SIZE) {
        throw new IllegalArgumentException(
                "Size must not exceed "
                        + ReaderSearchCriteria.MAX_PAGE_SIZE
                        + "."
        );
    }
}

private User requireCurrentUser() {
    User currentUser = getAuthenticatedUserPort.getCurrentUser();

    if (currentUser == null) {
        throw UnauthenticatedException.defaultMessage();
    }

    return currentUser;
}

private Long scopedCreatedByUserId(User currentUser) {
    return isAdmin(currentUser)
            ? null
            : currentUser.getId();
}

private boolean isAdmin(User user) {
    return user.getRoles() != null
            && user.getRoles().stream()
            .anyMatch(role ->
                    "ADMIN".equalsIgnoreCase(
                            role.getName()
                    )
            );
}

private String normalizeEmail(String email) {
    String normalizedEmail = normalizeRequired(
            email,
            "Email must not be blank."
    );

    return normalizedEmail.toLowerCase(
            Locale.ROOT
    );
}

private String normalizeRequired(
        String value,
        String errorMessage
) {
    if (value == null || value.isBlank()) {
        throw new IllegalArgumentException(
                errorMessage
        );
    }

    return value.trim();
}
    private ReaderResult mapToResult(Readers r) {
        String createdByName = resolveCreatedByName(r.getCreatedByUserId());
        return new ReaderResult(
                r.getId(),
                r.getCardNumber(),
                r.getName(),
                r.getEmail(),
                r.getPhoneNumber(),
                r.getAddress(),
                r.getCardStatus(),
                r.getCardIssuedAt(),
                r.getCardExpiryAt(),
                createdByName
        );
    }

    private String resolveCreatedByName(Long createdByUserId) {
        if (createdByUserId == null) {
            return "Hệ thống";
        }
        return findUserPort.findById(createdByUserId)
                .map(User::getFullName)
                .orElse("Thủ thư #" + createdByUserId);
    }
}
