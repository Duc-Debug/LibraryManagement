package org.example.librarymanagement.application.reader;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

import org.example.librarymanagement.domain.entity.Readers;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.ReaderAlreadyExistsException;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderResult;
import org.example.librarymanagement.port.inbound.reader.CreateReaderUseCase;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;

import lombok.RequiredArgsConstructor;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;

@RequiredArgsConstructor
public class ReaderManagementService implements CreateReaderUseCase {

    private final ReaderRepositoryPort readerRepositoryPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    @Override
    public CreateReaderResult createReader(CreateReaderCommand command) {
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
        Long creatorId = currentUser != null ? currentUser.getId() : null;

        // 4. Sinh Mã thẻ duy nhất (Format: RD-XXXXXX)
        String cardNumber = generateUniqueCardNumber();

        // 5. Khởi tạo Domain Object Readers (Mặc định trạng thái ACTIVE, hạn 1 năm, lưu vết creatorId)
        LocalDateTime now = LocalDateTime.now();
        LocalDate cardIssuedAt = LocalDate.now();
        LocalDate cardExpiryAt = cardIssuedAt.plusYears(1);

        Readers newReader = new Readers(
                null,
                cardNumber,
                command.name(),
                command.email(),
                command.phoneNumber(),
                command.address(),
                CardStatus.ACTIVE,
                cardIssuedAt,
                cardExpiryAt,
                now,
                now,
                true,
                creatorId
        );

        // 6. Lưu thông tin bạn đọc xuống DB qua Outbound Port
        Readers savedReader = readerRepositoryPort.save(newReader);

        // 7. Chuyển đổi và trả về Result DTO
        return new CreateReaderResult(
                savedReader.getId(),
                savedReader.getCardNumber(),
                savedReader.getName(),
                savedReader.getEmail(),
                savedReader.getPhoneNumber(),
                savedReader.getAddress(),
                savedReader.getCardStatus(),
                savedReader.getCardIssuedAt(),
                savedReader.getCardExpiryAt()
        );
    }

    @Override
    public java.util.List<CreateReaderResult> getAllReaders() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        java.util.List<Readers> readersList;

        if (currentUser != null) {
            boolean isAdmin = currentUser.getRoles() != null &&
                    currentUser.getRoles().stream().anyMatch(r -> "ADMIN".equalsIgnoreCase(r.getName()));

            if (!isAdmin) {
                // Thủ thư thường: Chỉ hiển thị các bạn đọc do chính tài khoản thủ thư này tạo ra
                readersList = readerRepositoryPort.findByCreatedByUserId(currentUser.getId());
            } else {
                // Admin hệ thống: Hiển thị tất cả bạn đọc
                readersList = readerRepositoryPort.findAll();
            }
        } else {
            readersList = readerRepositoryPort.findAll();
        }

        return readersList.stream()
                .map(r -> new CreateReaderResult(
                r.getId(),
                r.getCardNumber(),
                r.getName(),
                r.getEmail(),
                r.getPhoneNumber(),
                r.getAddress(),
                r.getCardStatus(),
                r.getCardIssuedAt(),
                r.getCardExpiryAt()
        ))
                .collect(java.util.stream.Collectors.toList());
    }

    private String generateUniqueCardNumber() {
        String cardNumber;
        Random random = new Random();
        do {
            int randomNumber = 100000 + random.nextInt(900000); // 6 chữ số ngẫu nhiên
            cardNumber = "RD-" + randomNumber;
        } while (readerRepositoryPort.existsByCardNumber(cardNumber));

        return cardNumber;
    }
}
