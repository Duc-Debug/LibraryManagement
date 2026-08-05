package org.example.librarymanagement.application.book;

import java.util.Optional;
import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteBookServiceTest {

    @Mock
    private LoadBookPort loadBookPort;

    @Mock
    private SaveBookPort saveBookPort;

    @Mock
    private CheckActiveBorrowPort checkActiveBorrowPort;

    private DeleteBookService deleteBookService;

    @BeforeEach
    void setUp() {
        deleteBookService = new DeleteBookService(loadBookPort, saveBookPort, checkActiveBorrowPort);
    }

    @Test
    @DisplayName("deleteBook: Xóa cứng thành công khi sách không thuộc phiếu mượn active")
    void deleteBook_Success() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setBookId(bookId);

        when(loadBookPort.findById(bookId)).thenReturn(Optional.of(book));
        when(checkActiveBorrowPort.hasActiveBorrowSlips(bookId)).thenReturn(false);

        // Act
        deleteBookService.deleteBook(bookId);

        // Assert
        verify(saveBookPort).deleteById(bookId);
    }

    @Test
    @DisplayName("deleteBook: Bị CHẶN khi sách đang có phiếu mượn active (Không được xóa DB)")
    void deleteBook_BlockedByPolicy() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle("Clean Code");

        when(loadBookPort.findById(bookId)).thenReturn(Optional.of(book));
        when(checkActiveBorrowPort.hasActiveBorrowSlips(bookId)).thenReturn(true);

        // Act & Assert
        assertThrows(DomainException.class, () -> deleteBookService.deleteBook(bookId));

        // Verify: Đảm bảo không được phép gọi deleteById
        verify(saveBookPort, never()).deleteById(any());
    }

    @Test
    @DisplayName("hideBook: Ẩn sách thành công, đổi active = false và lưu xuống DB")
    void hideBook_Success() {
        // Arrange
        Long bookId = 1L;
        Book book = new Book();
        book.setBookId(bookId);

        when(loadBookPort.findById(bookId)).thenReturn(Optional.of(book));
        when(checkActiveBorrowPort.hasActiveBorrowSlips(bookId)).thenReturn(false);

        // Act
        deleteBookService.hideBook(bookId);

        // Assert
        assertFalse(book.isActive());
        verify(saveBookPort).save(book);
    }
}
