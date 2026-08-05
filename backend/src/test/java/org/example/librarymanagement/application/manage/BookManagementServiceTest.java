package org.example.librarymanagement.application.manage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.inbound.manage.BookResult;
import org.example.librarymanagement.port.inbound.manage.UpdateBookCommand;
import org.example.librarymanagement.port.outbound.manage.BookRepository;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookManagementServiceTest {

    private BookRepository bookRepository;
    private GetAuthenticatedUserPort getAuthenticatedUserPort;
    private BookManagementService bookManagementService;

    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        getAuthenticatedUserPort = mock(GetAuthenticatedUserPort.class);

        bookManagementService = new BookManagementService(
                bookRepository,
                getAuthenticatedUserPort
        );
    }

    // =========================================================================
    // HAPPY PATHS (SỬA VÀ LƯU THÀNH CÔNG)
    // =========================================================================

    @Test
    @DisplayName("Sửa thông tin sách thành công khi dữ liệu hợp lệ và người dùng là Thủ thư")
    void updatesBookSuccessfullyWhenUserIsLibrarian() {
        UUID bookId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(librarian());

        Book existingBook = new Book(
                bookId, "Sách Cũ", "Tác Giả Cũ", "9786041123456",
                "Mô tả cũ", "http://image.com/old.jpg", "NXB Cũ", 2020,
                "Kệ A1", 10, 10, categoryId, true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbnAndIdNot("9786041123456", bookId)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateBookCommand command = new UpdateBookCommand(
                bookId, "Sách Mới", "Tác Giả Mới", "9786041123456",
                "Mô tả mới", "http://image.com/new.jpg", "NXB Mới", 2023,
                "Kệ B2", 15, categoryId
        );

        BookResult result = bookManagementService.updateBook(command);

        assertNotNull(result);
        assertEquals("Sách Mới", result.title());
        assertEquals("Tác Giả Mới", result.author());
        assertEquals(15, result.totalQuantity());
        assertEquals(15, result.availableQuantity());

        verify(bookRepository).save(argThat(book ->
                book.getTitle().equals("Sách Mới") &&
                book.getAuthor().equals("Tác Giả Mới") &&
                book.getTotalQuantity() == 15
        ));
    }

    @Test
    @DisplayName("Admin cũng có thể sửa thông tin sách thành công")
    void updatesBookSuccessfullyWhenUserIsAdmin() {
        UUID bookId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(admin());

        Book existingBook = new Book(
                bookId, "Sách Gốc", "Tác Giả", "9786041000000",
                null, null, null, 2021, "A1", 5, 5, categoryId, true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbnAndIdNot("9786041000000", bookId)).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UpdateBookCommand command = new UpdateBookCommand(
                bookId, "Sách Đã Sửa By Admin", "Tác Giả", "9786041000000",
                "Mô tả", null, "NXB", 2022, "A2", 5, categoryId
        );

        BookResult result = bookManagementService.updateBook(command);

        assertEquals("Sách Đã Sửa By Admin", result.title());
        verify(bookRepository).save(any(Book.class));
    }

    // =========================================================================
    // UNHAPPY PATHS (KIỂM TRA DỮ LIỆU KHÔNG HỢP LỆ & SECURITY)
    // =========================================================================

    @Test
    @DisplayName("Ném ngoại lệ khi không tìm thấy sách theo bookId")
    void throwsExceptionWhenBookNotFound() {
        UUID nonExistentBookId = UUID.randomUUID();

        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(librarian());
        when(bookRepository.findById(nonExistentBookId)).thenReturn(Optional.empty());

        UpdateBookCommand command = new UpdateBookCommand(
                nonExistentBookId, "Tiêu Đề", "Tác Giả", "9786041123456",
                null, null, null, 2020, null, 5, UUID.randomUUID()
        );

        DomainException exception = assertThrows(DomainException.class, () ->
                bookManagementService.updateBook(command)
        );

        assertTrue(exception.getMessage().contains("Not found"));
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ném ngoại lệ khi ISBN mới trùng với một cuốn sách khác trong DB")
    void throwsExceptionWhenIsbnAlreadyExistsOnAnotherBook() {
        UUID bookId = UUID.randomUUID();
        String duplicateIsbn = "9786041999999";

        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(librarian());

        Book existingBook = new Book(
                bookId, "Tên Sách", "Tác Giả", "9786041111111",
                null, null, null, 2020, null, 5, 5, UUID.randomUUID(), true,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));
        when(bookRepository.existsByIsbnAndIdNot(duplicateIsbn, bookId)).thenReturn(true);

        UpdateBookCommand command = new UpdateBookCommand(
                bookId, "Tên Sách", "Tác Giả", duplicateIsbn,
                null, null, null, 2020, null, 5, UUID.randomUUID()
        );

        DomainException exception = assertThrows(DomainException.class, () ->
                bookManagementService.updateBook(command)
        );

        assertTrue(exception.getMessage().contains("Duplicate ISBN"));
        verify(bookRepository, never()).save(any());
    }

    @Test
    @DisplayName("Từ chối thao tác khi người dùng có vai trò READER (Độc giả)")
    void rejectsUpdateWhenUserIsReader() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(reader());

        UpdateBookCommand command = new UpdateBookCommand(
                UUID.randomUUID(), "Tên Sách", "Tác Giả", "9786041123456",
                null, null, null, 2020, null, 5, UUID.randomUUID()
        );

        assertThrows(DomainException.class, () ->
                bookManagementService.updateBook(command)
        );

        verify(bookRepository, never()).save(any());
    }

    // =========================================================================
    // HELPER METHODS TẠO MOCK USER & ROLES
    // =========================================================================

    private User librarian() {
        Role roleLibrarian = new Role(2L, "LIBRARIAN", "Thủ thư");
        Role roleStaff = new Role(4L, "ROLE_STAFF", "Nhân viên");
        return new User(
                1L, "thuthu01", "hashedPass", "Thủ Thư A",
                "thuthu@gmail.com", "0987654321", true, null,
                LocalDateTime.now(), LocalDateTime.now(), Set.of(roleLibrarian, roleStaff)
        );
    }

    private User admin() {
        Role roleAdmin = new Role(1L, "ADMIN", "Quản trị viên");
        Role roleRoleAdmin = new Role(5L, "ROLE_ADMIN", "Quản trị viên");
        return new User(
                2L, "admin01", "hashedPass", "Admin B",
                "admin@gmail.com", "0987654322", true, null,
                LocalDateTime.now(), LocalDateTime.now(), Set.of(roleAdmin, roleRoleAdmin)
        );
    }

    private User reader() {
        Role role = new Role(3L, "READER", "Độc giả");
        return new User(
                3L, "reader01", "hashedPass", "Độc Giả C",
                "reader@gmail.com", "0987654323", true, null,
                LocalDateTime.now(), LocalDateTime.now(), Set.of(role)
        );
    }
}
