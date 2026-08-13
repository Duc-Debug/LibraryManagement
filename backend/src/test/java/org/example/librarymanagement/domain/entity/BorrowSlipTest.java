package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BorrowSlipTest {

    private static final String VALID_CODE = "BRW-1001";
    private static final Long VALID_READER_ID = 10L;
    private static final Long VALID_STAFF_ID = 100L;

    @Test
    @DisplayName("create: Khởi tạo phiếu mượn thành công với thông tin hợp lệ")
    void create_Success() {
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, "Ghi chú");

        assertNotNull(slip);
        assertEquals(VALID_CODE, slip.getBorrowCode());
        assertEquals(VALID_READER_ID, slip.getReaderId());
        assertEquals(VALID_STAFF_ID, slip.getCreatedByUserId());
        assertEquals("Ghi chú", slip.getNote());
        assertEquals(BorrowSlipStatus.BORROWING, slip.getStatus());
        assertNotNull(slip.getBorrowDate());
        assertNotNull(slip.getDueDate());
    }

    @Test
    @DisplayName("create: Ném DomainException khi borrowCode bị rỗng hoặc null")
    void create_InvalidBorrowCode() {
        DomainException exNull = assertThrows(DomainException.class,
                () -> BorrowSlip.create(null, VALID_READER_ID, VALID_STAFF_ID, 14, null));
        assertEquals("Borrow code must not be blank", exNull.getMessage());

        DomainException exBlank = assertThrows(DomainException.class,
                () -> BorrowSlip.create("   ", VALID_READER_ID, VALID_STAFF_ID, 14, null));
        assertEquals("Borrow code must not be blank", exBlank.getMessage());
    }

    @Test
    @DisplayName("create: Ném DomainException khi readerId là null hoặc <= 0")
    void create_InvalidReaderId() {
        DomainException exNull = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, null, VALID_STAFF_ID, 14, null));
        assertEquals("Reader ID must be greater than 0", exNull.getMessage());

        DomainException exZero = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, 0L, VALID_STAFF_ID, 14, null));
        assertEquals("Reader ID must be greater than 0", exZero.getMessage());

        DomainException exNeg = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, -1L, VALID_STAFF_ID, 14, null));
        assertEquals("Reader ID must be greater than 0", exNeg.getMessage());
    }

    @Test
    @DisplayName("create: Ném DomainException khi createdByUserId là null hoặc <= 0")
    void create_InvalidCreatedByUserId() {
        DomainException exNull = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, VALID_READER_ID, null, 14, null));
        assertEquals("Created by user ID must be greater than 0", exNull.getMessage());

        DomainException exZero = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, VALID_READER_ID, 0L, 14, null));
        assertEquals("Created by user ID must be greater than 0", exZero.getMessage());

        DomainException exNeg = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, VALID_READER_ID, -1L, 14, null));
        assertEquals("Created by user ID must be greater than 0", exNeg.getMessage());
    }

    @Test
    @DisplayName("create: Khởi tạo phiếu mượn thành công với thời điểm xác định (Deterministic Time)")
    void create_WithExplicitBorrowDate_Success() {
        LocalDateTime specificDate = LocalDateTime.of(2026, 8, 1, 10, 0);
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null, specificDate);

        assertNotNull(slip);
        assertEquals(specificDate, slip.getBorrowDate());
        assertEquals(LocalDateTime.of(2026, 8, 15, 10, 0), slip.getDueDate());
    }

    @Test
    @DisplayName("create: Ném DomainException khi borrowDays <= 0")
    void create_InvalidBorrowDays() {
        DomainException exZero = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 0, null));
        assertEquals("Borrow days must be greater than 0", exZero.getMessage());

        DomainException exNeg = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, -5, null));
        assertEquals("Borrow days must be greater than 0", exNeg.getMessage());
    }

    @Test
    @DisplayName("create: Ném DomainException khi borrowDate là null")
    void create_NullBorrowDate() {
        DomainException exception = assertThrows(DomainException.class,
                () -> BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null, null));
        assertEquals("Borrow date must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("markReturned: Đánh dấu trả sách thành công và cập nhật returnDate")
    void markReturned_Success() {
        LocalDateTime borrowDate = LocalDateTime.of(2026, 8, 1, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 10, 10, 0);
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null, borrowDate);

        slip.markReturned(returnDate);

        assertEquals(BorrowSlipStatus.RETURNED, slip.getStatus());
        assertEquals(returnDate, slip.getReturnDate());
    }

    @Test
    @DisplayName("markReturned: Ném DomainException khi phiếu đã ở trạng thái RETURNED")
    void markReturned_ThrowsDomainException_WhenAlreadyReturned() {
        LocalDateTime borrowDate = LocalDateTime.of(2026, 8, 1, 10, 0);
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null, borrowDate);
        slip.markReturned();

        DomainException exception = assertThrows(
                DomainException.class,
                () -> slip.markReturned(LocalDateTime.of(2026, 8, 15, 10, 0))
        );
        assertEquals("Borrow slip has already been returned", exception.getMessage());
    }

    @Test
    @DisplayName("markReturned: Ném DomainException khi ngày trả trước ngày mượn")
    void markReturned_ThrowsDomainException_WhenReturnDateBeforeBorrowDate() {
        LocalDateTime borrowDate = LocalDateTime.of(2026, 8, 10, 10, 0);
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null, borrowDate);

        DomainException exception = assertThrows(
                DomainException.class,
                () -> slip.markReturned(LocalDateTime.of(2026, 8, 5, 10, 0))
        );
        assertEquals("Return date cannot be before borrow date", exception.getMessage());
    }

    @Test
    @DisplayName("markOverdue: Đánh dấu quá hạn thành công khi phiếu đang mượn")
    void markOverdue_Success() {
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null);
        slip.markOverdue();

        assertEquals(BorrowSlipStatus.OVERDUE, slip.getStatus());
    }

    @Test
    @DisplayName("markOverdue: Ném DomainException khi phiếu đã được trả")
    void markOverdue_ThrowsDomainException_WhenAlreadyReturned() {
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null);
        slip.markReturned();

        DomainException exception = assertThrows(DomainException.class, slip::markOverdue);
        assertEquals("Cannot mark a returned borrow slip as overdue", exception.getMessage());
    }

    @Test
    @DisplayName("markOverdue: Idempotent khi phiếu đã ở trạng thái OVERDUE")
    void markOverdue_Idempotent_WhenAlreadyOverdue() {
        BorrowSlip slip = BorrowSlip.create(VALID_CODE, VALID_READER_ID, VALID_STAFF_ID, 14, null);
        slip.markOverdue();
        slip.markOverdue(); // Không ném lỗi

        assertEquals(BorrowSlipStatus.OVERDUE, slip.getStatus());
    }
}
