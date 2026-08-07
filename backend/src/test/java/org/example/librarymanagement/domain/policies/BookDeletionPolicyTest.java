package org.example.librarymanagement.domain.policies;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookDeletionPolicyTest {

    private Book createSampleBook(Long id, String title) {
        return new Book(
                id,
                title,
                "Robert C. Martin",
                "978-0134494166",
                "Mô tả sách",
                "https://example.com/cover.jpg",
                "NXB Tri Thức",
                (short) 2024,
                "Kệ A1-01",
                5,
                5,
                1L,
                true,
                LocalDateTime.now(),
                LocalDateTime.now());
    }

    @Test
    @DisplayName("Cho phép Xóa/Ẩn sách khi KHÔNG CÓ phiếu mượn chưa hoàn trả")
    void shouldAllowDeletionWhenNoActiveBorrowSlips() {
        // Arrange
        Book book = createSampleBook(1L, "Lập trình Java DDD");
        boolean hasActiveBorrowSlips = false;

        // Act & Assert
        assertDoesNotThrow(() -> BookDeletionPolicy.validateCanDeleteOrHide(book, hasActiveBorrowSlips));
    }

    @Test
    @DisplayName("CHẶN Xóa/Ẩn sách và ném DomainException khi ĐANG CÓ phiếu mượn chưa hoàn trả")
    void shouldBlockDeletionWhenActiveBorrowSlipsExist() {
        // Arrange
        Book book = createSampleBook(1L, "Lập trình Java DDD");
        boolean hasActiveBorrowSlips = true;

        // Act & Assert
        DomainException exception = assertThrows(
                DomainException.class,
                () -> BookDeletionPolicy.validateCanDeleteOrHide(book, hasActiveBorrowSlips));

        // Kiểm tra thông điệp lỗi nghiệp vụ A2.4
        assertEquals(
                "Không thể xóa hoặc ẩn sách 'Lập trình Java DDD' (ID: 1) vì sách đang nằm trong phiếu mượn chưa hoàn trả (Đang mượn hoặc Quá hạn).",
                exception.getMessage());
    }
}
