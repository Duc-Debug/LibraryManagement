package org.example.librarymanagement.application.book;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.domain.policies.UniqueIsbnPolicy;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.book.UpdateBookCommand;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.file.FileCleanupPort;
import org.example.librarymanagement.port.outbound.file.FileStoragePort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;

/**
 * Application Service: UpdateBookService
 * Pure Java 100% - Hexagonal Architecture Implementation
 */
public class UpdateBookService implements UpdateBookUseCase {

    private final BookRepositoryPort bookRepository;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;
    private final FileStoragePort fileStoragePort;
    private final FileCleanupPort fileCleanupPort;

    public UpdateBookService(
            BookRepositoryPort bookRepository,
            GetAuthenticatedUserPort getAuthenticatedUserPort,
            FileStoragePort fileStoragePort,
            FileCleanupPort fileCleanupPort
    ) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepositoryPort must not be null");
        this.getAuthenticatedUserPort = Objects.requireNonNull(getAuthenticatedUserPort, "GetAuthenticatedUserPort must not be null");
        this.fileStoragePort = Objects.requireNonNull(fileStoragePort, "FileStoragePort must not be null");
        this.fileCleanupPort = Objects.requireNonNull(fileCleanupPort, "FileCleanupPort must not be null");
    }

    @Override
    public BookResult updateBook(UpdateBookCommand command) {
        if (command == null) {
            throw new InvalidBookDataException("Update book command must not be null");
        }

        verifyStaffAccess();

        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException(command.bookId()));

        boolean isIsbnExisted = bookRepository.existsByIsbnAndIdNot(command.isbn(), command.bookId());
        UniqueIsbnPolicy.validateIsbnForUpdate(isIsbnExisted, command.isbn());

        String finalCoverImageUrl = command.coverImageUrl();

        if (command.imageStream() != null && command.originalFilename() != null && command.size() > 0) {
            String uploadedUrl = fileStoragePort.storeBookImage(
                    command.imageStream(),
                    command.originalFilename(),
                    command.size()
            );

            if (book.getCoverImageUrl() != null && !book.getCoverImageUrl().isBlank()) {
                fileCleanupPort.queueFileForDeletion(book.getCoverImageUrl());
            }

            finalCoverImageUrl = uploadedUrl;
        }

        book.updateDetails(
                command.title(),
                command.author(),
                command.isbn(),
                command.description(),
                finalCoverImageUrl,
                command.publisher(),
                command.publishedYear(),
                command.shelfLocation(),
                command.totalQuantity(),
                command.categoryId()
        );

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
                book.getUpdatedAt()
        );
    }
}
