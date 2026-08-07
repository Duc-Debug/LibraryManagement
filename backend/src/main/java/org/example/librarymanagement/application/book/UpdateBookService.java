package org.example.librarymanagement.application.book;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.UpdateBookCommand;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepository;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;

public class UpdateBookService implements UpdateBookUseCase {
  
    private final BookRepository bookRepository;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public UpdateBookService(BookRepository bookRepository, GetAuthenticatedUserPort getAuthenticatedUserPort) {
        this.bookRepository = bookRepository;
        this.getAuthenticatedUserPort = getAuthenticatedUserPort;
    }

    @Override
    public BookResult updateBook(UpdateBookCommand command) {
        verifyStaffAccess();

        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new DomainException("Not found: Book with ID " + command.bookId() + " does not exist."));

        boolean isIsbnExisted = bookRepository.existsByIsbnAndIdNot(command.isbn(), command.bookId());
        if (isIsbnExisted) {
            throw new DomainException("Duplicate ISBN: The ISBN " + command.isbn() + " is already used by another book.");
        }

        book.updateDetails(command.title(), command.author(), command.isbn(), command.description(),
                command.coverImageUrl(), command.publisher(), command.publishedYear(),
                command.shelfLocation(), command.totalQuantity(), 
                command.categoryId());

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
                book.getUpdatedAt()
        );
    }
}
