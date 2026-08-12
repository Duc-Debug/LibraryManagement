package org.example.librarymanagement.application.borrow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.reader.ReaderNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.domain.policies.BorrowSlipCreationPolicy;
import org.example.librarymanagement.port.dtos.borrow.OverdueSlipSummaryDto;
import org.example.librarymanagement.port.dtos.borrow.ReaderBorrowEligibilityDto;
import org.example.librarymanagement.port.inbound.borrow.CheckBorrowEligibilityUseCase;
import org.example.librarymanagement.port.outbound.borrow.LoadReaderBorrowStatusPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.setting.LoadSystemSettingPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class CheckBorrowEligibilityService implements CheckBorrowEligibilityUseCase {

    public static final String SETTING_KEY_MAX_BORROW_LIMIT = "MAX_CONCURRENT_BORROW_BOOKS";

    private final ReaderRepositoryPort readerRepositoryPort;
    private final LoadReaderBorrowStatusPort loadReaderBorrowStatusPort;
    private final LoadSystemSettingPort loadSystemSettingPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public CheckBorrowEligibilityService(
            ReaderRepositoryPort readerRepositoryPort,
            LoadReaderBorrowStatusPort loadReaderBorrowStatusPort,
            LoadSystemSettingPort loadSystemSettingPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.readerRepositoryPort = Objects.requireNonNull(readerRepositoryPort, "ReaderRepositoryPort must not be null");
        this.loadReaderBorrowStatusPort = Objects.requireNonNull(loadReaderBorrowStatusPort, "LoadReaderBorrowStatusPort must not be null");
        this.loadSystemSettingPort = Objects.requireNonNull(loadSystemSettingPort, "LoadSystemSettingPort must not be null");
        this.getAuthenticatedUserPort = Objects.requireNonNull(getAuthenticatedUserPort, "GetAuthenticatedUserPort must not be null");
    }

    @Override
    public ReaderBorrowEligibilityDto checkEligibility(Long readerId) {
        verifyStaffAccess();

        if (readerId == null || readerId <= 0) {
            throw new ValidationException("Reader ID must be greater than 0");
        }

        Reader reader = readerRepositoryPort.findById(readerId)
                .orElseThrow(() -> ReaderNotFoundException.withId(readerId));

        int maxLimit = loadSystemSettingPort.getIntSetting(
                SETTING_KEY_MAX_BORROW_LIMIT,
                BorrowSlipCreationPolicy.DEFAULT_MAX_CONCURRENT_BORROW_LIMIT
        );

        int currentBorrowingCount = loadReaderBorrowStatusPort.countCurrentBorrowingBooks(readerId);
        int remainingLimit = Math.max(0, maxLimit - currentBorrowingCount);

        // 1. Kiểm tra trạng thái tài khoản & thẻ độc giả
        if (!reader.isActive()) {
            return ReaderBorrowEligibilityDto.rejected(
                    reader.getId(), reader.getCardNumber(), reader.getName(),
                    currentBorrowingCount, maxLimit, remainingLimit,
                    false, 0, List.of(),
                    "Tài khoản độc giả đã bị vô hiệu hóa trong hệ thống."
            );
        }
        if (reader.getCardStatus() == CardStatus.LOCKED) {
            return ReaderBorrowEligibilityDto.rejected(
                    reader.getId(), reader.getCardNumber(), reader.getName(),
                    currentBorrowingCount, maxLimit, remainingLimit,
                    false, 0, List.of(),
                    "Thẻ độc giả đang bị khóa (LOCKED), không thể mượn sách."
            );
        }
        if (reader.getCardStatus() == CardStatus.EXPIRED) {
            return ReaderBorrowEligibilityDto.rejected(
                    reader.getId(), reader.getCardNumber(), reader.getName(),
                    currentBorrowingCount, maxLimit, remainingLimit,
                    false, 0, List.of(),
                    "Thẻ độc giả đã hết hạn (EXPIRED), vui lòng gia hạn thẻ trước khi mượn sách."
            );
        }

        // 2. Kiểm tra sách quá hạn chưa trả
        List<OverdueSlipSummaryDto> overdueSlips = loadReaderBorrowStatusPort.findOverdueSlipsByReaderId(
                readerId, LocalDateTime.now()
        );
        if (!overdueSlips.isEmpty()) {
            int overdueBooksCount = overdueSlips.stream()
                    .mapToInt(OverdueSlipSummaryDto::unreturnedBooksCount)
                    .sum();
            return ReaderBorrowEligibilityDto.rejected(
                    reader.getId(), reader.getCardNumber(), reader.getName(),
                    currentBorrowingCount, maxLimit, remainingLimit,
                    true, overdueBooksCount, overdueSlips,
                    String.format("Độc giả đang có %d cuốn sách quá hạn chưa hoàn trả. Vui lòng xử lý trả sách và đóng phạt trước khi mượn tiếp.", overdueBooksCount)
            );
        }

        // 3. Kiểm tra đã đạt hạn mức mượn tối đa
        if (currentBorrowingCount >= maxLimit) {
            return ReaderBorrowEligibilityDto.rejected(
                    reader.getId(), reader.getCardNumber(), reader.getName(),
                    currentBorrowingCount, maxLimit, 0,
                    false, 0, List.of(),
                    String.format("Độc giả đã đạt hạn mức mượn sách tối đa (%d/%d cuốn). Vui lòng trả bớt sách trước khi mượn thêm.", currentBorrowingCount, maxLimit)
            );
        }

        // 4. Đủ điều kiện mượn sách
        return ReaderBorrowEligibilityDto.eligible(
                reader.getId(),
                reader.getCardNumber(),
                reader.getName(),
                currentBorrowingCount,
                maxLimit,
                remainingLimit
        );
    }

    @Override
    public void validateBorrowEligibility(Long readerId, int requestedBooksCount) {
        verifyStaffAccess();

        if (readerId == null || readerId <= 0) {
            throw new ValidationException("Reader ID must be greater than 0");
        }
        if (requestedBooksCount <= 0) {
            throw new ValidationException("Requested books count must be greater than 0");
        }

        Reader reader = readerRepositoryPort.findById(readerId)
                .orElseThrow(() -> ReaderNotFoundException.withId(readerId));

        // 1. Kiểm tra trạng thái thẻ & tài khoản độc giả
        BorrowSlipCreationPolicy.validateCanBorrow(reader);

        // 2. Chặn nếu có sách quá hạn chưa trả
        boolean hasOverdue = loadReaderBorrowStatusPort.hasOverdueBooks(readerId, LocalDateTime.now());
        BorrowSlipCreationPolicy.validateNoOverdueBooks(hasOverdue, reader.getCardNumber());

        // 3. Chặn nếu số sách mượn đồng thời vượt hạn mức
        int maxLimit = loadSystemSettingPort.getIntSetting(
                SETTING_KEY_MAX_BORROW_LIMIT,
                BorrowSlipCreationPolicy.DEFAULT_MAX_CONCURRENT_BORROW_LIMIT
        );
        int currentBorrowingCount = loadReaderBorrowStatusPort.countCurrentBorrowingBooks(readerId);
        BorrowSlipCreationPolicy.validateBorrowLimit(currentBorrowingCount, requestedBooksCount, maxLimit);
    }

    private void verifyStaffAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }
}
