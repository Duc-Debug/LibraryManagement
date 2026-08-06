package org.example.librarymanagement.application.manage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;


import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.inbound.manage.BookResult;
import org.example.librarymanagement.port.inbound.manage.UpdateBookCommand;
import org.example.librarymanagement.port.outbound.manage.BookRepository;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
public class BookManagementServiceTest {
 
    private BookRepository bookRepository;
    private GetAuthenticatedUserPort getAuthenticatedUserPort;

    // đối tượng cần test
    private BookManagementService bookManagementService;

    // hàm setUp chạy trước mỗi method 
    @BeforeEach
    void setUp() {
        bookRepository = mock(BookRepository.class);
        getAuthenticatedUserPort = mock(GetAuthenticatedUserPort.class);

        //khoi tao Service cần test
        bookManagementService = new BookManagementService(bookRepository, getAuthenticatedUserPort);
    }


    @Test
    @DisplayName("Test updateBook method - Success case")
    void givenValidDataAndLibrarianRole_whenUpdateBook_thenReturnUpdatedBookResult() {
        // Arrange
        Long bookId = 1L;
        Long categoryId = 1L;


      //  giả lập data
        User mockLibrarian = createMockUser("LIBRARIAN");
       when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(mockLibrarian);

      // Giả lập data book 
              Book existingBookInDb = new Book(
                bookId,
                "Lập Trình Java Căn Bản",     // Title cũ
                "Nguyễn Văn A",               // Author cũ
                "978-604-1-00000-1",          // ISBN cũ
                "Mô tả sách cũ",              // Description
                "http://image.com/old.jpg",   // CoverUrl
                "NXB Giáo Dục",               // Publisher
                2020,                         // PublishedYear
                "Kệ A1-01",                    // ShelfLocation
                10,                           // TotalQuantity (Tổng số lượng: 10)
                10,                           // AvailableQuantity (Có sẵn: 10)
                categoryId,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );


        // cấu hình mock cho repository
        // khi tìm theo bookId -> trả về cuốn sách trên
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBookInDb));
        

        // khi check ISBN mowis -> trả về false (không trùng)
        when(bookRepository.existsByIsbnAndIdNot("978-604-1-00000-2", bookId)).thenReturn(false);


        // khi gọi hàm save(...) -> trả về chính đối tượng sách được truyền vào
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // dữ liệu mới thủ thư muốn cập nhật
        UpdateBookCommand updateCommand = new UpdateBookCommand(
                bookId,
                "Lập Trình Java Nâng Cao",    // Title mới
                "Nguyễn Văn B",               // Author mới
                "978-604-1-00000-2",          // ISBN mới
                "Mô tả sách mới",              // Description mới
                "http://image.com/new.jpg",   // CoverUrl mới
                "NXB Khoa Học",               // Publisher mới
                2024,                         // PublishedYear mới
                "Kệ A1-02",                    // ShelfLocation mới
                15,                           // TotalQuantity mới (Tổng số lượng: 15)
                categoryId
        );

        // Act
        BookResult result = bookManagementService.updateBook(updateCommand);

        // Assert - kiểm tra kết quả và hành vi
        assertNotNull(result, "The result should not be null");


        // so sánh dữ liệu thực tế thu được với dữ liệu kỳ vọng
        assertEquals(bookId, result.bookId());
         assertEquals("Lập Trình Java Nâng Cao (Tái Bản)", result.title());
        assertEquals("Nguyễn Văn A (Hiệu đính)", result.author());
        assertEquals("978-604-1-99999-9", result.isbn());
        assertEquals(2024, result.publishedYear());
        assertEquals("Kệ B2-10", result.shelfLocation());
        assertEquals(15, result.totalQuantity());
        assertEquals(15, result.availableQuantity()); // 10 cũ + (15 mới - 10 cũ) = 15
        //Xác nhận rằng lệnh lưu DB (save) đã được gọi đúng 1 lần với dữ liệu mới
        verify(bookRepository, times(1)).save(argThat(savedBook ->
                savedBook.getTitle().equals("Lập Trình Java Nâng Cao (Tái Bản)") &&
                savedBook.getAuthor().equals("Nguyễn Văn A (Hiệu đính)") &&
                savedBook.getTotalQuantity() == 15));

        
    }

    // hàm giúp tạo mock user với role
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
