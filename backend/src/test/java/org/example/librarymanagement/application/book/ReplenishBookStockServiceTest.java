package org.example.librarymanagement.application.book;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockCommand;
import org.example.librarymanagement.port.outbound.book.BookRepository;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReplenishBookStockServiceTest {

    private BookRepository bookRepository;
    private GetAuthenticatedUserPort getAuthenticatedUserPort;
    private ReplenishBookStockService replenishBookStockService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        getAuthenticatedUserPort = mock(GetAuthenticatedUserPort.class);
        replenishBookStockService = new ReplenishBookStockService(bookRepository, getAuthenticatedUserPort);
    }

    @Test
    @DisplayName("Test replenishStock - Success case")
    void givenValidQuantityAndLibrarianRole_whenReplenishStock_thenReturnUpdatedBookResult() {
        // Arrange
        Long bookId = 1L;
        User mockLibrarian = createMockUser("LIBRARIAN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(mockLibrarian);

        Book existingBook = new Book(
                bookId,
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                "Mô tả",
                "http://image.com/clean.jpg",
                "NXB Prentice Hall",
                (short) 2017,
                "Kệ B2-04",
                10, // total
                7,  // available (borrowed: 3)
                1L,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(bookRepository.findByIdForUpdate(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReplenishBookStockCommand command = new ReplenishBookStockCommand(bookId, 5);

        // Act
        BookResult result = replenishBookStockService.replenishStock(command);

        // Assert
        assertNotNull(result);
        assertEquals(15, result.totalQuantity());
        assertEquals(12, result.availableQuantity()); // 7 + 5

        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    @DisplayName("Test replenishStock - Invalid quantity throws exception")
    void givenInvalidQuantity_whenReplenishStock_thenThrowInvalidBookDataException() {
        // Arrange
        Long bookId = 1L;
        User mockLibrarian = createMockUser("LIBRARIAN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(mockLibrarian);

        Book existingBook = new Book(
                bookId,
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                "Mô tả",
                "http://image.com/clean.jpg",
                "NXB Prentice Hall",
                (short) 2017,
                "Kệ B2-04",
                10,
                7,
                1L,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(bookRepository.findByIdForUpdate(bookId)).thenReturn(Optional.of(existingBook));

        ReplenishBookStockCommand command = new ReplenishBookStockCommand(bookId, 0);

        // Act & Assert
        assertThrows(InvalidBookDataException.class, () -> {
            replenishBookStockService.replenishStock(command);
        });
    }

    @Test
    @DisplayName("Test replenishStock - Book not found throws exception")
    void givenNonExistentBookId_whenReplenishStock_thenThrowDomainException() {
        // Arrange
        Long bookId = 99L;
        User mockLibrarian = createMockUser("LIBRARIAN");
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(mockLibrarian);

        when(bookRepository.findByIdForUpdate(bookId)).thenReturn(Optional.empty());

        ReplenishBookStockCommand command = new ReplenishBookStockCommand(bookId, 5);

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> {
            replenishBookStockService.replenishStock(command);
        });
    }

    private User createMockUser(String roleName) {
        Role role = new Role(1L, roleName, "Vai trò " + roleName);
        return new User(
                100L,
                "thuthu01",
                "hashed_password",
                "Thủ Thư Nguyễn Văn B",
                "thuthu@gmail.com",
                "0912345678",
                true,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(role)
        );
    }
}
