package org.example.librarymanagement.application.book;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DuplicateResourceException;
import org.example.librarymanagement.domain.exceptions.ResourceNotFoundException;
import org.example.librarymanagement.domain.exceptions.ValidationException;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.example.librarymanagement.port.outbound.file.FileStoragePort;

public class CreateBookService implements CreateBookUseCase {

    private final SaveBookPort saveBookPort;
    private final BookRepositoryPort bookRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final FileStoragePort fileStoragePort;

    public CreateBookService(
            SaveBookPort saveBookPort,
            BookRepositoryPort bookRepositoryPort,
            CategoryRepositoryPort categoryRepositoryPort,
            FileStoragePort fileStoragePort
    ) {
        this.saveBookPort = Objects.requireNonNull(saveBookPort, "SaveBookPort must not be null");
        this.bookRepositoryPort = Objects.requireNonNull(bookRepositoryPort, "BookRepositoryPort must not be null");
        this.categoryRepositoryPort = Objects.requireNonNull(categoryRepositoryPort, "CategoryRepositoryPort must not be null");
        this.fileStoragePort = Objects.requireNonNull(fileStoragePort, "FileStoragePort must not be null");
    }

    @Override
    public BookResult createBook(CreateBookCommand command) {
        if (command == null) {
            throw new ValidationException("Command tạo sách không được để trống");
        }
        if (command.title() == null || command.title().isBlank()) {
            throw new ValidationException("Book title must not be null");
        }
        if (command.author() == null || command.author().isBlank()) {
            throw new ValidationException("Author must not be null");
        }
        if (command.isbn() == null || command.isbn().isBlank()) {
            throw new ValidationException("ISBN must not be null");
        }
        if (command.categoryId() == null) {
            throw new ValidationException("Category must not be null");
        }
        if (command.totalQuantity() <= 0) {
            throw new ValidationException("Total quantity must larger than 0");
        }

        String normalizedIsbn = command.isbn().trim().toUpperCase().replace("-", "");

        if (bookRepositoryPort.existsByIsbn(normalizedIsbn)) {
            throw new DuplicateResourceException("Book with ISBN " + normalizedIsbn + " already have.");
        }

       if (!categoryRepositoryPort.existsById(command.categoryId())) {
    throw new ResourceNotFoundException(
            "Category with ID " + command.categoryId() + " does not exist."
    );
}

        String imageUrl = null;
        try {
            if (command.imageInputStream() != null) {
                imageUrl = fileStoragePort.storeBookImage(
                        command.imageInputStream(),
                        command.imageFilename(),
                        command.imageSize()
                );
            }

            Book book = Book.create(
                    command.title(),
                    command.author(),
                    normalizedIsbn, 
                    command.description(),
                    imageUrl,
                    command.publisher(),
                   toPublishedYear(command.publishedYear()),
                    command.shelfLocation(),
                    command.totalQuantity(),
                    command.categoryId()
            );

            Book savedBook = saveBookPort.save(book);
            return mapToResult(savedBook);

        } catch (Exception e) {
            if (imageUrl != null) {
                try {
                    fileStoragePort.deleteFile(imageUrl);
                } catch (Exception cleanupError) {
                    e.addSuppressed(cleanupError);
                }
            }
            throw e;
        }
    }
   private Short toPublishedYear(Integer publishedYear) {
    if (publishedYear == null) {
        return null;
    }

    int currentYear = java.time.Year.now().getValue();

    if (publishedYear < 1000 || publishedYear > currentYear) {
        throw new ValidationException(
                "Published year must be between 1000 and " + currentYear
        );
    }

    return publishedYear.shortValue();
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
