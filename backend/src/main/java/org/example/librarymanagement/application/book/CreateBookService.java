package org.example.librarymanagement.application.book;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DuplicateResourceException;
import org.example.librarymanagement.domain.exceptions.ResourceNotFoundException;
import org.example.librarymanagement.domain.exceptions.ValidationException;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;

public class CreateBookService implements CreateBookUseCase {

    private final LoadBookPort loadBookPort;
    private final SaveBookPort saveBookPort;
    private final CategoryRepositoryPort categoryRepositoryPort;

    public CreateBookService(
            LoadBookPort loadBookPort, 
            SaveBookPort saveBookPort, 
            CategoryRepositoryPort categoryRepositoryPort
    ) {
        this.loadBookPort = Objects.requireNonNull(loadBookPort, "LoadBookPort must not be null");
        this.saveBookPort = Objects.requireNonNull(saveBookPort, "SaveBookPort must not be null");
        this.categoryRepositoryPort = Objects.requireNonNull(categoryRepositoryPort, "CategoryRepositoryPort must not be null");
    }

    @Override
    public BookResult createBook(CreateBookCommand command) {

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

        if (loadBookPort.existsByIsbn(normalizedIsbn)) {
            throw new DuplicateResourceException("Book with ISBN " + normalizedIsbn + " already have.");
        }

        if (categoryRepositoryPort.findById(command.categoryId()).isEmpty()) {
            throw new ResourceNotFoundException("Category with ID " + command.categoryId() + " not exist.");
        }

        Book book = Book.create(
                command.title(),
                command.author(),
                normalizedIsbn, 
                command.description(),
                command.coverImageUrl(),
                command.publisher(),
                command.publishedYear() != null ? command.publishedYear().shortValue() : null,
                command.shelfLocation(),
                command.totalQuantity(),
                command.categoryId()
        );

        Book savedBook = saveBookPort.save(book);
        return mapToResult(savedBook);
    }

    @Override
    public List<BookResult> getAllBooks(int page, int size) {
        return loadBookPort.findAll(page, size)
                .stream()
                .map(this::mapToResult)
                .filter(Objects::nonNull)
                .toList();
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