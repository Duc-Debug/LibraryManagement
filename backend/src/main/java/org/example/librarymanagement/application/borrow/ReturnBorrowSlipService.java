package org.example.librarymanagement.application.borrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.BorrowDetails;
import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.borrow.BorrowSlipNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.dtos.borrow.ReturnBorrowSlipResponseDto;
import org.example.librarymanagement.port.inbound.borrow.ReturnBorrowSlipUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.BorrowDetailsRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class ReturnBorrowSlipService implements ReturnBorrowSlipUseCase {

    private final LoadBorrowSlipPort loadBorrowSlipPort;
    private final SaveBorrowSlipPort saveBorrowSlipPort;
    private final BorrowDetailsRepositoryPort borrowDetailsRepositoryPort;
    private final BookRepositoryPort bookRepositoryPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public ReturnBorrowSlipService(
            LoadBorrowSlipPort loadBorrowSlipPort,
            SaveBorrowSlipPort saveBorrowSlipPort,
            BorrowDetailsRepositoryPort borrowDetailsRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.loadBorrowSlipPort =
                Objects.requireNonNull(loadBorrowSlipPort);

        this.saveBorrowSlipPort =
                Objects.requireNonNull(saveBorrowSlipPort);

        this.borrowDetailsRepositoryPort =
                Objects.requireNonNull(borrowDetailsRepositoryPort);

        this.bookRepositoryPort =
                Objects.requireNonNull(bookRepositoryPort);

        this.getAuthenticatedUserPort =
                Objects.requireNonNull(getAuthenticatedUserPort);
    }

    @Override
    public ReturnBorrowSlipResponseDto returnBorrowSlip(Long borrowSlipId) {

        User currentUser = requireStaff();

        validateBorrowSlipId(borrowSlipId);

        BorrowSlip borrowSlip =
                loadBorrowSlipPort
                        .findByIdForUpdate(borrowSlipId)
                        .orElseThrow(() ->
                                new BorrowSlipNotFoundException(
                                        borrowSlipId
                                )
                        );

       if (borrowSlip.getStatus() == BorrowSlipStatus.RETURNED) {
    throw new DomainException(
            "Borrow slip has already been returned"
    );
}

        List<BorrowDetails> details =
                borrowDetailsRepositoryPort
                        .findByBorrowSlipIdForUpdate(
                                borrowSlipId
                        );

        if (details.isEmpty()) {
            throw new DomainException(
                    "Borrow slip has no borrowed books"
            );
        }

        LocalDateTime returnTime =
                LocalDateTime.now();

        int returnedBooks = 0;
        BigDecimal totalFine =
                BigDecimal.ZERO;

        for (BorrowDetails detail : details) {

            if (detail.isReturned()) {
                continue;
            }

            Book book =
                    bookRepositoryPort
                            .findByIdForUpdate(
                                    detail.getBookId()
                            )
                            .orElseThrow(() ->
                                    new DomainException(
                                            "Book not found with ID: "
                                                    + detail.getBookId()
                                    )
                            );

            BigDecimal fineAmount =
                    BigDecimal.ZERO;

            String fineReason = null;

            detail.markReturned(
                    currentUser.getId(),
                    returnTime,
                    fineAmount,
                    fineReason
            );

            book.increaseAvailableQuantity();

            bookRepositoryPort.save(book);

            totalFine =
                    totalFine.add(fineAmount);

            returnedBooks++;
        }

        if (returnedBooks == 0) {
            throw new DomainException(
                    "All books in this borrow slip have already been returned"
            );
        }

        borrowDetailsRepositoryPort.saveAll(
                details
        );

        borrowSlip.markReturned(
                returnTime
        );

        saveBorrowSlipPort.save(
                borrowSlip
        );

        return new ReturnBorrowSlipResponseDto(
                borrowSlip.getId(),
                borrowSlip.getStatus(),
                returnedBooks,
                returnTime,
                totalFine
        );
    }

    private User requireStaff() {

    User currentUser = getAuthenticatedUserPort.getCurrentUser();

    if (currentUser == null) {
        throw new UnauthenticatedException("Authentication is required");
    }

    AccountLockPolicy.validateAccountActive(currentUser);
    AuthorizationAccessPolicy.validateStaffAccess(currentUser);

    return currentUser;
}

    private void validateBorrowSlipId(
            Long borrowSlipId
    ) {
        if (borrowSlipId == null
                || borrowSlipId <= 0) {

            throw new ValidationException(
                    "Borrow slip ID must be greater than 0"
            );
        }
    }
}