package org.example.librarymanagement.application.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.FineType;
import org.example.librarymanagement.domain.exceptions.borrow.BorrowSlipNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.domain.policies.FineCalculationPolicy;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class CalculateFineService implements CalculateFineUseCase {

    private final LoadBorrowSlipPort loadBorrowSlipPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public CalculateFineService(
            LoadBorrowSlipPort loadBorrowSlipPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.loadBorrowSlipPort = Objects.requireNonNull(
                loadBorrowSlipPort,
                "LoadBorrowSlipPort must not be null"
        );
        this.getAuthenticatedUserPort = Objects.requireNonNull(
                getAuthenticatedUserPort,
                "GetAuthenticatedUserPort must not be null"
        );
    }

    @Override
    public FineCalculationResponseDto calculateBorrowSlipFine(Long borrowSlipId, LocalDateTime returnDate) {
        // 1. Kiểm tra xác thực và phân quyền (chỉ Thủ thư hoặc Quản trị viên hoạt động mới được truy cập)
        verifyStaffAccess();

        // 2. Validate tham số đầu vào
        if (borrowSlipId == null || borrowSlipId <= 0) {
            throw new ValidationException("Borrow slip ID must be greater than 0");
        }

        // 3. Ngày trả tính toán (nếu không truyền -> mặc định thời điểm hiện tại)
        LocalDateTime effectiveReturnDate = (returnDate != null) ? returnDate : LocalDateTime.now();

        // 4. Lấy chi tiết phiếu mượn từ Outbound Port
        BorrowSlipResponseDto slip = loadBorrowSlipPort.findSlipDetailById(borrowSlipId)
                .orElseThrow(() -> new BorrowSlipNotFoundException(borrowSlipId));

        // 5. Áp dụng Domain Policy tính số ngày trễ và tiền phạt quá hạn
        long overdueDays = FineCalculationPolicy.calculateOverdueDays(slip.dueAt(), effectiveReturnDate);
        boolean isOverdue = overdueDays > 0;
        int totalBooks = slip.totalBooks() > 0 ? slip.totalBooks() : 1;

        BigDecimal overdueFineAmount = FineCalculationPolicy.calculateOverdueFine(
                slip.dueAt(),
                effectiveReturnDate,
                totalBooks,
                FineCalculationPolicy.DEFAULT_DAILY_OVERDUE_RATE
        );

        BigDecimal totalFineAmount = overdueFineAmount;

        String fineReason = isOverdue
                ? FineCalculationPolicy.formatFineReason(
                        FineType.OVERDUE,
                        String.format("Quá hạn %d ngày (%d cuốn sách)", overdueDays, totalBooks)
                )
                : null;

        // 6. Trả về kết quả hoàn chỉnh cho Controller / Frontend
        return new FineCalculationResponseDto(
                slip.id(),
                slip.borrowCode(),
                slip.readerId(),
                slip.readerCardNumber(),
                slip.readerName(),
                slip.borrowedAt(),
                slip.dueAt(),
                effectiveReturnDate,
                overdueDays,
                totalBooks,
                FineCalculationPolicy.DEFAULT_DAILY_OVERDUE_RATE,
                overdueFineAmount,
                totalFineAmount,
                isOverdue,
                fineReason
        );
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