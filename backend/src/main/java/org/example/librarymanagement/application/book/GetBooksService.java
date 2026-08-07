package org.example.librarymanagement.application.book;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;
import org.example.librarymanagement.port.dtos.book.BookResponseDto;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.category.LoadCategoryPort;

public class GetBooksService implements GetBooksUseCase {
    private final LoadBookPort loadBookPort;
    private final LoadCategoryPort loadCategoryPort;

    public GetBooksService(LoadBookPort loadBookPort, LoadCategoryPort loadCategoryPort) {
        this.loadBookPort = Objects.requireNonNull(loadBookPort, "LoadBookPort must be not null");
        this.loadCategoryPort = Objects.requireNonNull(loadCategoryPort, "LoadCategoryPort must be not null");
    }

    @Override
    public PageResult<BookResponseDto> getBooks(int page, int size, String keyword) {
        int pageNumber = Math.max(0, page);
        int pageSize = size <= 0 ? 10 : size;
        String searchKeyword = keyword != null ? keyword.trim() : "";

        PageResult<Book> domainPageResult = loadBookPort.findAll(pageNumber, pageSize, searchKeyword);

        // 1. Batch resolving category names (Chống N+1 Query)
        Set<Long> categoryIds = domainPageResult.content().stream()
                .map(Book::getCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, String> categoryNameMap = loadCategoryPort.findCategoryNamesByIds(categoryIds);

        // 2. Map sang BookResponseDto
        List<BookResponseDto> dtoList = domainPageResult.content().stream()
                .map(book -> mapToResponseDto(book, categoryNameMap.get(book.getCategoryId())))
                .toList();

        return new PageResult<>(
                dtoList,
                domainPageResult.page(),
                domainPageResult.size(),
                domainPageResult.totalElements(),
                domainPageResult.totalPages());
    }

    @Override
    public BookResponseDto getBookById(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new InvalidBookDataException("ID sách không hợp lệ.");
        }
        Book book = loadBookPort.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        String categoryName = null;
        if (book.getCategoryId() != null) {
            Map<Long, String> categoryNameMap = loadCategoryPort.findCategoryNamesByIds(Set.of(book.getCategoryId()));
            categoryName = categoryNameMap.get(book.getCategoryId());
        }

        return mapToResponseDto(book, categoryName);
    }

    private BookResponseDto mapToResponseDto(Book book, String categoryName) {
        return new BookResponseDto(
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
                categoryName != null ? categoryName : "Chưa phân loại",
                book.isActive(),
                book.getCreatedAt()
        );
    }
}
