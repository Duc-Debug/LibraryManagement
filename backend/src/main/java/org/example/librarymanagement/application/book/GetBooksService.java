package org.example.librarymanagement.application.book;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;
import org.example.librarymanagement.port.inbound.book.BookResponseDto;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.example.librarymanagement.port.inbound.common.PageResult;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;

public class GetBooksService implements GetBooksUseCase {
    private final LoadBookPort loadBookPort;

    public GetBooksService(LoadBookPort loadBookPort) {
        this.loadBookPort = Objects.requireNonNull(loadBookPort, "LoadBookPort must be not null");
    }

    @Override
    public PageResult<BookResponseDto> getBooks(int page, int size, String keyword) {
        int pageNumber = Math.max(0, page);
        int pageSize = size <= 0 ? 10 : size;
        String searchKeyword = keyword != null ? keyword.trim() : "";

        PageResult<Book> domainPageResult = loadBookPort.findAll(pageNumber, pageSize, searchKeyword);

        List<BookResponseDto> dtoList = domainPageResult.getItems().stream().map(this::mapToResponseDto).toList();

        return new PageResult<>(
                dtoList,
                domainPageResult.getPage(),
                domainPageResult.getSize(),
                domainPageResult.getTotalElements(),
                domainPageResult.getTotalPages());
    }

    @Override
    public BookResponseDto getBookById(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new InvalidBookDataException("ID sách không hợp lệ.");
        }
        Book book = loadBookPort.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
        return mapToResponseDto(book);
    }

    private BookResponseDto mapToResponseDto(Book book) {
        return new BookResponseDto(
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
                book.getCreatedAt()
        );
    }
}
