package org.example.librarymanagement.application.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.FineType;
import org.example.librarymanagement.domain.exceptions.DomainException;
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
            GetAuthenticatedUserPort getAuthenticatedUserPort) {
        this.loadBorrowSlipPort = loadBorrowSlipPort;
        this.getAuthenticatedUserPort = getAuthenticatedUserPort;
    }

    @Override
    public FineCalculationResponseDto calculateBorrowSlipFine(Long borrowSlipId, LocalDateTime returnDate) {
        verifyStaffAccess();

        if (borrowSlipId == null || borrowSlipId <= 0) {
            throw new ValidationException("Borrow slip ID must be greater than 0");
        }

        BorrowSlipResponseDto slip = loadBorrowSlipPort.findSlipDetailById(borrowSlipId)
                .orElseThrow(() -> new BorrowSlipNotFoundException(borrowSlipId));

        LocalDateTime actualReturnDate = (returnDate != null) ? returnDate : LocalDateTime.now();

        if (slip.totalBooks() <= 0) {
            throw new DomainException(
                    "Borrow slip has no borrowed books to calculate fine (totalBooks must be greater than 0)"
            );
        }
        int totalBooks = slip.totalBooks();

        long overdueDays = FineCalculationPolicy.calculateOverdueDays(slip.dueAt(), actualReturnDate);
        boolean isOverdue = overdueDays > 0;

        BigDecimal overdueFine = FineCalculationPolicy.calculateOverdueFine(
                slip.dueAt(),
                actualReturnDate,
                totalBooks,
                FineCalculationPolicy.DEFAULT_DAILY_OVERDUE_RATE
        );

        String fineReason = null;
        if (isOverdue) {
            String detail = String.format("Quá hạn %d ngày (%d cuốn sách)", overdueDays, totalBooks);
            fineReason = FineCalculationPolicy.formatFineReason(FineType.OVERDUE, detail);
        }

        return new FineCalculationResponseDto(
                slip.id(),
                slip.borrowCode(),
                slip.readerId(),
                slip.readerCardNumber(),
                slip.readerName(),
                slip.borrowedAt(),
                slip.dueAt(),
                actualReturnDate,
                overdueDays,
                totalBooks,
                FineCalculationPolicy.DEFAULT_DAILY_OVERDUE_RATE,
                overdueFine,
                overdueFine,
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