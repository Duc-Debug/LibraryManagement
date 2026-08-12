package org.example.librarymanagement.application.borrow;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.BorrowDetails;
import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.domain.exceptions.reader.ReaderNotFoundException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.CreateBorrowSlipCommand;
import org.example.librarymanagement.port.inbound.borrow.CheckBorrowEligibilityUseCase;
import org.example.librarymanagement.port.inbound.borrow.CreateBorrowSlipUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.BorrowDetailsRepositoryPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;
import org.example.librarymanagement.port.outbound.reader.ReaderRepositoryPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

import jakarta.validation.ValidationException;

public class CreateBorrowSlipService implements CreateBorrowSlipUseCase {
    private final CheckBorrowEligibilityUseCase checkBorrowUseCase;
    private final ReaderRepositoryPort readerRepositoryPort;
    private final BookRepositoryPort bookRepositoryPort;
    private final SaveBorrowSlipPort saveBorrowSlipPort;
    private final BorrowDetailsRepositoryPort borrowDetailsRepositoryPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public CreateBorrowSlipService(
            CheckBorrowEligibilityUseCase checkBorrowUseCase,
            ReaderRepositoryPort readerRepositoryPort,
            BookRepositoryPort bookRepositoryPort,
            SaveBorrowSlipPort saveBorrowSlipPort,
            BorrowDetailsRepositoryPort borrowDetailsRepositoryPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort) {
        this.checkBorrowUseCase = Objects.requireNonNull(checkBorrowUseCase,
                "CheckBorrowEligibilityUseCase must not be null");
        this.readerRepositoryPort = Objects.requireNonNull(readerRepositoryPort,
                "ReaderRepositoryPort must not be null");
        this.bookRepositoryPort = Objects.requireNonNull(bookRepositoryPort, "BookRepositoryPort must not be null");
        this.saveBorrowSlipPort = Objects.requireNonNull(saveBorrowSlipPort, "SaveBorrowSlipPort must not be null");
        this.borrowDetailsRepositoryPort = Objects.requireNonNull(borrowDetailsRepositoryPort,
                "BorrowDetailsRepositoryPort must not be null");
        this.getAuthenticatedUserPort = Objects.requireNonNull(getAuthenticatedUserPort,
                "GetAuthenticatedUserPort must not be null");
    }

    @Override
    public BorrowSlipResponseDto createBorrowSlip(CreateBorrowSlipCommand command) {
        User currentUser = requireStaff();

        if (command == null) {
            throw new ValidationException("Command must not be null");
        }

        Reader reader = readerRepositoryPort.findById(command.readerId())
                .orElseThrow(() -> ReaderNotFoundException.withId(command.readerId()));

        checkBorrowUseCase.validateBorrowEligibility(command.readerId(), command.bookIds().size());

        List<Book> booksToBorrow = new ArrayList<>();
        for (Long bookId : command.bookIds()) {
            Book book = bookRepositoryPort.findByIdForUpdate(bookId)
                    .orElseThrow(() -> new BookNotFoundException(bookId));

            if (!book.isAvailableForBorrow()) {
                throw new DomainException("Book '" + book.getTitle() + "' is out of stock or inactive");
            }

            book.decreaseAvailableQuantity();
            bookRepositoryPort.save(book);
            booksToBorrow.add(book);
        }

        // Sinh mã phiếu(VD: BM202608121415001)
        String borrowCode = generateBorrowCode();

        int borrowDays = (command.borrowDays() != null && command.borrowDays() > 0) ? command.borrowDays() : 14;

        BorrowSlip borrowSlip = BorrowSlip.create(borrowCode, reader.getId(), currentUser.getId(), borrowDays,
                command.note());

        BorrowSlip savedSlip = saveBorrowSlipPort.save(borrowSlip);

        List<BorrowDetails> detailsList = new ArrayList<>();

        for (Book book : booksToBorrow) {
            BorrowDetails detail = BorrowDetails.create(savedSlip.getId(), book.getId());
            detailsList.add(detail);
        }
        borrowDetailsRepositoryPort.saveAll(detailsList);

        return new BorrowSlipResponseDto(
                savedSlip.getId(),
                savedSlip.getBorrowCode(),
                reader.getId(),
                reader.getCardNumber(),
                reader.getName(),
                currentUser.getId(),
                currentUser.getUsername(),
                savedSlip.getBorrowDate(),
                savedSlip.getDueDate(),
                savedSlip.getStatus(),
                savedSlip.getNote(),
                detailsList.size(),
                savedSlip.getCreatedAt());
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

    private String generateBorrowCode() {
        return "BM" + System.currentTimeMillis();
    }
}