package org.example.librarymanagement.application.manage;



import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.domain.policies.UniqueIsbnPolicy;
import org.example.librarymanagement.port.inbound.manage.BookResult;
import org.example.librarymanagement.port.inbound.manage.UpdateBookCommand;
import org.example.librarymanagement.port.inbound.manage.UpdateBookUseCase;
import org.example.librarymanagement.port.outbound.manage.BookRepository;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public class BookManagementService implements UpdateBookUseCase {
  
    private final BookRepository bookRepository;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

  public BookManagementService(BookRepository bookRepository, GetAuthenticatedUserPort getAuthenticatedUserPort) {
        this.bookRepository = bookRepository;
        this.getAuthenticatedUserPort = getAuthenticatedUserPort;
    }

    @Override
    
    public BookResult updateBook(UpdateBookCommand command) {
    
        // kiem tra phan quyen
        verifyStaffAccess();

        // tim sach trong csdl
        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new DomainException("Not found: Book with ID " + command.bookId() + " does not exist."));

                // kiem tra isbn co bi trung voi sach khac khong
        boolean isIsbnExisted = bookRepository.existsByIsbnAndIdNot(command.isbn(), command.bookId());
        if(isIsbnExisted) {
            throw new DomainException("Duplicate ISBN: The ISBN " + command.isbn() + " is already used by another book.");
        }

        // cap nhat thong tin sach
        book.updateInfor(command.title(), command.author(), command.isbn(), command.description(),
                command.coverImageUrl(), command.publisher(), command.publishedYear(),
                command.shelfLocation(), command.totalQuantity(), 
                command.categoryId());

                // luu thong tin sach vao csdl
        Book updatedBook = bookRepository.save(book);

        // tra ve ket qua
        return mapToResult(updatedBook);
    

    

    }

    private void verifyStaffAccess()
    {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (!currentUser.hasRole("ROLE_STAFF") && !currentUser.hasRole("ROLE_ADMIN")) {
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
