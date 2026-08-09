package org.example.librarymanagement.application.book;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.book.ReplenishBookStockCommand;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

public class ReplenishBookStockService implements ReplenishBookStockUseCase {

    private final BookRepositoryPort bookRepository;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public ReplenishBookStockService(BookRepositoryPort bookRepository,
            GetAuthenticatedUserPort getAuthenticatedUserPort) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepositoryPort must not be null");
        this.getAuthenticatedUserPort = Objects.requireNonNull(getAuthenticatedUserPort,
                "GetAuthenticatedUserPort must not be null");
    }

    @Override
    public BookResult replenishStock(ReplenishBookStockCommand command) {
        if (command == null) {
            throw new InvalidBookDataException("Replenish book stock command must not be null");
        }

        verifyStaffAccess();

        Book book = bookRepository.findByIdForUpdate(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));

        book.restock(command.quantityToAdd());

        Book updatedBook = bookRepository.save(book);

        return mapToResult(updatedBook);
    }

    private void verifyStaffAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }

    private BookResult mapToResult(Book book) {
        return new BookResult(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getDescription(),
                book.getCoverImageUrl(),
                book.getPublisher(),
                book.getPublishedYear(),
                book.getShelfLocation(),
                book.getTotalQuantity(),
                book.getAvailableQuantity(),
                book.getCategoryId(),
                book.isActive(),
                book.getCreatedAt(),
                book.getUpdatedAt());
    }
}