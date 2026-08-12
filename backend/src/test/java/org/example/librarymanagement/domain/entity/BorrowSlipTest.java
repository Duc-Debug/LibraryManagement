package org.example.librarymanagement.domain.entity;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BorrowSlipTest {

    @Test
    @DisplayName("create: Khởi tạo phiếu mượn thành công với readerId hợp lệ")
    void create_Success() {
        BorrowSlip slip = BorrowSlip.create(1L, 14);

        assertNotNull(slip);
        assertEquals(1L, slip.getReaderId());
        assertEquals(BorrowSlipStatus.BORROWING, slip.getStatus());
        assertNotNull(slip.getBorrowDate());
        assertNotNull(slip.getDueDate());
    }

    @Test
    @DisplayName("create: Ném DomainException khi readerId là null hoặc <= 0")
    void create_InvalidReaderId() {
        DomainException exNull = assertThrows(DomainException.class, () -> BorrowSlip.create(null, 14));
        assertEquals("Reader ID must be greater than 0", exNull.getMessage());

        DomainException exZero = assertThrows(DomainException.class, () -> BorrowSlip.create(0L, 14));
        assertEquals("Reader ID must be greater than 0", exZero.getMessage());

        DomainException exNeg = assertThrows(DomainException.class, () -> BorrowSlip.create(-1L, 14));
        assertEquals("Reader ID must be greater than 0", exNeg.getMessage());
    }

    @Test
    @DisplayName("create: Khởi tạo phiếu mượn thành công với thời điểm xác định (Deterministic Time)")
    void create_WithExplicitBorrowDate_Success() {
        java.time.LocalDateTime specificDate = java.time.LocalDateTime.of(2026, 8, 1, 10, 0);
        BorrowSlip slip = BorrowSlip.create(1L, 14, specificDate);

        assertNotNull(slip);
        assertEquals(specificDate, slip.getBorrowDate());
        assertEquals(java.time.LocalDateTime.of(2026, 8, 15, 10, 0), slip.getDueDate());
    }

    @Test
    @DisplayName("create: Ném DomainException khi borrowDays <= 0")
    void create_InvalidBorrowDays() {
        DomainException exZero = assertThrows(DomainException.class, () -> BorrowSlip.create(1L, 0));
        assertEquals("Borrow days must be greater than 0", exZero.getMessage());

        DomainException exNeg = assertThrows(DomainException.class, () -> BorrowSlip.create(1L, -5));
        assertEquals("Borrow days must be greater than 0", exNeg.getMessage());
    }

    @Test
    @DisplayName("create: Ném DomainException khi borrowDate là null")
    void create_NullBorrowDate() {
        DomainException exception = assertThrows(DomainException.class, () -> BorrowSlip.create(1L, 14, null));
        assertEquals("Borrow date must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("markReturned: Đánh dấu trả sách thành công và cập nhật returnDate")
    void markReturned_Success() {
        java.time.LocalDateTime borrowDate = java.time.LocalDateTime.of(2026, 8, 1, 10, 0);
        java.time.LocalDateTime returnDate = java.time.LocalDateTime.of(2026, 8, 10, 10, 0);
        BorrowSlip slip = BorrowSlip.create(1L, 14, borrowDate);

        slip.markReturned(returnDate);

        assertEquals(BorrowSlipStatus.RETURNED, slip.getStatus());
        assertEquals(returnDate, slip.getReturnDate());
    }

    @Test
    @DisplayName("markReturned: Ném DomainException khi phiếu đã ở trạng thái RETURNED")
    void markReturned_ThrowsDomainException_WhenAlreadyReturned() {
        java.time.LocalDateTime borrowDate = java.time.LocalDateTime.of(2026, 8, 1, 10, 0);
        BorrowSlip slip = BorrowSlip.create(1L, 14, borrowDate);
        slip.markReturned();

        DomainException exception = assertThrows(
                DomainException.class,
                () -> slip.markReturned(java.time.LocalDateTime.of(2026, 8, 15, 10, 0))
        );
        assertEquals("Borrow slip has already been returned", exception.getMessage());
    }

    @Test
    @DisplayName("markReturned: Ném DomainException khi ngày trả trước ngày mượn")
    void markReturned_ThrowsDomainException_WhenReturnDateBeforeBorrowDate() {
        java.time.LocalDateTime borrowDate = java.time.LocalDateTime.of(2026, 8, 10, 10, 0);
        BorrowSlip slip = BorrowSlip.create(1L, 14, borrowDate);

        DomainException exception = assertThrows(
                DomainException.class,
                () -> slip.markReturned(java.time.LocalDateTime.of(2026, 8, 5, 10, 0))
        );
        assertEquals("Return date cannot be before borrow date", exception.getMessage());
    }

    @Test
    @DisplayName("markOverdue: Đánh dấu quá hạn thành công khi phiếu đang mượn")
    void markOverdue_Success() {
        BorrowSlip slip = BorrowSlip.create(1L, 14);
        slip.markOverdue();

        assertEquals(BorrowSlipStatus.OVERDUE, slip.getStatus());
    }

    @Test
    @DisplayName("markOverdue: Ném DomainException khi phiếu đã được trả")
    void markOverdue_ThrowsDomainException_WhenAlreadyReturned() {
        BorrowSlip slip = BorrowSlip.create(1L, 14);
        slip.markReturned();

        DomainException exception = assertThrows(DomainException.class, slip::markOverdue);
        assertEquals("Cannot mark a returned borrow slip as overdue", exception.getMessage());
    }

    @Test
    @DisplayName("markOverdue: Idempotent khi phiếu đã ở trạng thái OVERDUE")
    void markOverdue_Idempotent_WhenAlreadyOverdue() {
        BorrowSlip slip = BorrowSlip.create(1L, 14);
        slip.markOverdue();
        slip.markOverdue(); // Không ném lỗi

        assertEquals(BorrowSlipStatus.OVERDUE, slip.getStatus());
    }
}
