package org.example.librarymanagement.application.book;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockCommand;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepository;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;

public class ReplenishBookStockService implements ReplenishBookStockUseCase {

    private final BookRepository bookRepository;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public ReplenishBookStockService(BookRepository bookRepository, GetAuthenticatedUserPort getAuthenticatedUserPort) {
        this.bookRepository = bookRepository;
        this.getAuthenticatedUserPort = getAuthenticatedUserPort;
    }

    @Override
    public BookResult replenishStock(ReplenishBookStockCommand command) {
        verifyStaffAccess();

        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new DomainException("Not found: Book with ID " + command.bookId() + " does not exist."));

        book.replenishStock(command.quantityToAdd());

        Book updatedBook = bookRepository.save(book);

        return mapToResult(updatedBook);
    }

    private void verifyStaffAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        
        boolean isAuthorized = currentUser.hasRole("LIBRARIAN") 
                            || currentUser.hasRole("ADMIN")
                            || currentUser.hasRole("ROLE_STAFF") 
                            || currentUser.hasRole("ROLE_ADMIN");

        if (!isAuthorized) {
            throw new DomainException("Error Security: User does not have permission to perform this action.");
        }

        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }

    private BookResult mapToResult(Book book) {
        return new BookResult(
                book.getBookId(),
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
                book.getUpdatedAt()
        );
    }
}
