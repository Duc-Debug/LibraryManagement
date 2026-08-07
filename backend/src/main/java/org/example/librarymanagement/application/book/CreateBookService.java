package org.example.librarymanagement.application.book;

import java.util.List;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DuplicateResourceException;
import org.example.librarymanagement.domain.exceptions.ResourceNotFoundException;
import org.example.librarymanagement.domain.exceptions.ValidationException;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.example.librarymanagement.port.outbound.book.FindBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreateBookService implements CreateBookUseCase {

    private final FindBookPort findBookPort;
    private final SaveBookPort saveBookPort;
    private final CategoryRepositoryPort categoryRepositoryPort; // Dùng đúng port chứa findById của team

    public CreateBookService(
            FindBookPort findBookPort, 
            SaveBookPort saveBookPort, 
            CategoryRepositoryPort categoryRepositoryPort
    ) {
        this.findBookPort = findBookPort;
        this.saveBookPort = saveBookPort;
        this.categoryRepositoryPort = categoryRepositoryPort;
    }

    @Override
    public BookResult createBook(CreateBookCommand command) {

        if (command.title() == null || command.title().isBlank()) {
            throw new ValidationException("Tên sách không được để trống");
        }
        if (command.author() == null || command.author().isBlank()) {
            throw new ValidationException("Tác giả không được để trống");
        }
        if (command.isbn() == null || command.isbn().isBlank()) {
            throw new ValidationException("ISBN không được để trống");
        }
        if (command.categoryId() == null) {
            throw new ValidationException("Thể loại không được để trống");
        }
        if (command.totalQuantity() <= 0) {
            throw new ValidationException("Số lượng sách phải lớn hơn 0");
        }
        if (command.coverImageUrl() == null || command.coverImageUrl().isBlank()) {
            throw new ValidationException("Ảnh bìa không được để trống");
        }

        String normalizedIsbn = command.isbn().trim().toUpperCase().replace("-", "");

        if (findBookPort.existsByIsbn(normalizedIsbn)) {
            throw new DuplicateResourceException("Sách với ISBN " + normalizedIsbn + " đã tồn tại.");
        }

        // Tận dụng hàm findById của team an toàn tuyệt đối
        if (categoryRepositoryPort.findById(command.categoryId()).isEmpty()) {
            throw new ResourceNotFoundException("Thể loại với ID " + command.categoryId() + " không tồn tại.");
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
        return toResultModel(savedBook);
    }

    @Override
    public List<BookResult> getAllBooks(int page, int size) {
        return findBookPort.findAll(page, size)
                .stream()
                .map(this::toResultModel)
                .toList();
    }

    private BookResult toResultModel(Book book) {
        return new BookResult(
                book.getBookId(), 
                book.getTitle(),
                book.getAuthor(),
                book.getIsbn(),
                book.getCoverImageUrl(),
                book.getTotalQuantity(),
                book.getAvailableQuantity(),
                book.getCategoryId(),
                book.isActive()
        );
    }
}