package org.example.librarymanagement.application.book;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;
import org.example.librarymanagement.port.inbound.book.BookResponseDto;
import org.example.librarymanagement.port.inbound.common.PageResult;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBooksServiceTest {

    @Mock
    private LoadBookPort loadBookPort;

    private GetBooksService getBooksService;

    @BeforeEach
    void setUp() {
        getBooksService = new GetBooksService(loadBookPort);
    }

    private Book createSampleBook(Long id, String title) {
        return new Book(
                id,
                title,
                "Robert C. Martin",
                "978-0134494166",
                "Clean Architecture Description",
                "https://example.com/cover.jpg",
                "Prentice Hall",
                (short) 2017,
                "Shelf A1",
                10,
                8,
                1L,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("getBooks: Lấy danh sách sách phân trang thành công")
    void getBooks_Success() {
        // Arrange
        Book book1 = createSampleBook(1L, "Clean Code");
        Book book2 = createSampleBook(2L, "Clean Architecture");
        PageResult<Book> mockDomainPage = new PageResult<>(List.of(book1, book2), 0, 10, 2L, 1);

        when(loadBookPort.findAll(0, 10, "Clean")).thenReturn(mockDomainPage);

        // Act
        PageResult<BookResponseDto> result = getBooksService.getBooks(0, 10, "Clean");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getItems().size());
        assertEquals("Clean Code", result.getItems().get(0).title());
        assertEquals(8, result.getItems().get(0).availableQuantity());
        assertEquals(10, result.getItems().get(0).totalQuantity());
        verify(loadBookPort).findAll(0, 10, "Clean");
    }

    @Test
    @DisplayName("getBooks: Tự động chuẩn hóa tham số page âm hoặc size <= 0")
    void getBooks_Normalization() {
        // Arrange
        PageResult<Book> mockEmptyPage = new PageResult<>(List.of(), 0, 10, 0L, 0);
        when(loadBookPort.findAll(0, 10, "")).thenReturn(mockEmptyPage);

        // Act
        PageResult<BookResponseDto> result = getBooksService.getBooks(-1, 0, null);

        // Assert
        assertNotNull(result);
        verify(loadBookPort).findAll(0, 10, "");
    }

    @Test
    @DisplayName("getBookById: Tìm thấy chi tiết sách theo ID")
    void getBookById_Success() {
        // Arrange
        Long bookId = 1L;
        Book book = createSampleBook(bookId, "Refactoring");
        when(loadBookPort.findById(bookId)).thenReturn(Optional.of(book));

        // Act
        BookResponseDto dto = getBooksService.getBookById(bookId);

        // Assert
        assertNotNull(dto);
        assertEquals(bookId, dto.bookId());
        assertEquals("Refactoring", dto.title());
        assertEquals(8, dto.availableQuantity());
        verify(loadBookPort).findById(bookId);
    }

    @Test
    @DisplayName("getBookById: Ném InvalidBookDataException khi ID null hoặc <= 0")
    void getBookById_InvalidId() {
        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> getBooksService.getBookById(null));
        assertThrows(InvalidBookDataException.class, () -> getBooksService.getBookById(0L));
        assertThrows(InvalidBookDataException.class, () -> getBooksService.getBookById(-5L));
    }

    @Test
    @DisplayName("getBookById: Ném BookNotFoundException khi không tìm thấy sách")
    void getBookById_NotFound() {
        // Arrange
        Long bookId = 99L;
        when(loadBookPort.findById(bookId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> getBooksService.getBookById(bookId));
        verify(loadBookPort).findById(bookId);
    }
}
