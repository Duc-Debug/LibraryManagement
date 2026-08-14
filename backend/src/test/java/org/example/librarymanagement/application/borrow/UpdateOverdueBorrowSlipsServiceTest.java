package org.example.librarymanagement.application.borrow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.borrow.SaveBorrowSlipPort;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateOverdueBorrowSlipsServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 8, 13);
    private static final LocalDate YESTERDAY = TODAY.minusDays(1);
    private static final LocalDate TOMORROW = TODAY.plusDays(1);

    @Mock
    private LoadBorrowSlipPort loadBorrowSlipPort;

    @Mock
    private SaveBorrowSlipPort saveBorrowSlipPort;

    private UpdateOverdueBorrowSlipsService service;

    @BeforeEach
    void setUp() {
        service = new UpdateOverdueBorrowSlipsService(loadBorrowSlipPort, saveBorrowSlipPort);
    }

    // ==================== HELPER ====================

    /**
     * Tạo BorrowSlip với trạng thái và dueDate tuỳ ý, dùng constructor public
     * để tránh phụ thuộc vào LocalDateTime.now() trong BorrowSlip.create().
     */
    private BorrowSlip makeBorrowingSlip(long id, LocalDate dueDate) {
        LocalDateTime borrowedAt = LocalDateTime.of(2026, 7, 1, 10, 0);
        LocalDateTime dueAt = dueDate.atTime(23, 59, 59);
        return new BorrowSlip(
                id,
                "BRW-" + id,
                10L,
                1L,
                borrowedAt,
                dueAt,
                null,
                BorrowSlipStatus.BORROWING,
                null,
                borrowedAt,
                borrowedAt);
    }

    private BorrowSlip makeReturnedSlip(long id, LocalDate dueDate) {
        LocalDateTime borrowedAt = LocalDateTime.of(2026, 7, 1, 10, 0);
        LocalDateTime dueAt = dueDate.atTime(23, 59, 59);
        LocalDateTime returnedAt = LocalDateTime.of(2026, 8, 10, 10, 0);
        return new BorrowSlip(
                id,
                "BRW-" + id,
                10L,
                1L,
                borrowedAt,
                dueAt,
                returnedAt,
                BorrowSlipStatus.RETURNED,
                null,
                borrowedAt,
                returnedAt);
    }

    // ==================== TESTS ====================

    @Test
    @DisplayName("execute: Cập nhật sang OVERDUE khi có phiếu BORROWING quá hạn (yesterday)")
    void execute_UpdatesOverdueSlips_WhenBorrowingAndPastDue() {
        // Arrange — 2 phiếu BORROWING, dueDate = yesterday
        BorrowSlip slip1 = makeBorrowingSlip(1L, YESTERDAY);
        BorrowSlip slip2 = makeBorrowingSlip(2L, YESTERDAY);
        when(loadBorrowSlipPort.findAllActivePastDue(TODAY)).thenReturn(List.of(slip1, slip2));
        when(saveBorrowSlipPort.saveAll(anyList())).thenReturn(List.of(slip1, slip2));

        // Act
        int updated = service.executeOn(TODAY);

        // Assert
        assertEquals(2, updated);
        assertEquals(BorrowSlipStatus.OVERDUE, slip1.getStatus());
        assertEquals(BorrowSlipStatus.OVERDUE, slip2.getStatus());
        verify(saveBorrowSlipPort).saveAll(List.of(slip1, slip2));
    }

    @Test
    @DisplayName("execute: Không gọi saveAll khi không có phiếu quá hạn")
    void execute_DoesNotCallSaveAll_WhenNoOverdueSlips() {
        // Arrange
        when(loadBorrowSlipPort.findAllActivePastDue(TODAY)).thenReturn(List.of());

        // Act
        int updated = service.executeOn(TODAY);

        // Assert
        assertEquals(0, updated);
        verify(saveBorrowSlipPort, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("execute: Phiếu BORROWING dueDate = today KHÔNG bị đánh dấu OVERDUE")
    void execute_SkipsTodaySlips() {
        // Arrange — query chỉ trả về slips có dueAt < referenceDate
        // → today slips không được trả về bởi port → service không xử lý
        when(loadBorrowSlipPort.findAllActivePastDue(TODAY)).thenReturn(List.of());

        // Act
        int updated = service.executeOn(TODAY);

        // Assert
        assertEquals(0, updated);
        verify(saveBorrowSlipPort, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("execute: Phiếu BORROWING dueDate = tomorrow KHÔNG bị đánh dấu OVERDUE")
    void execute_SkipsTomorrowSlips() {
        // Arrange — tomorrow slips không được port trả về
        when(loadBorrowSlipPort.findAllActivePastDue(TODAY)).thenReturn(List.of());

        // Act
        int updated = service.executeOn(TODAY);

        // Assert
        assertEquals(0, updated);
        verify(saveBorrowSlipPort, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("execute: Phiếu RETURNED đã quá hạn KHÔNG bị xử lý (port không load RETURNED)")
    void execute_SkipsReturnedSlips() {
        // Arrange — RETURNED slips sẽ không được trả về bởi query (WHERE status='BORROWING')
        // → service không nhận được → không gọi saveAll
        when(loadBorrowSlipPort.findAllActivePastDue(TODAY)).thenReturn(List.of());

        // Act
        int updated = service.executeOn(TODAY);

        // Assert
        assertEquals(0, updated);
        verify(saveBorrowSlipPort, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("execute: Idempotent — phiếu đã OVERDUE không được load lại bởi port")
    void execute_IsIdempotent_OverdueSlipsNotReloaded() {
        // Arrange — OVERDUE slips không nằm trong điều kiện WHERE status='BORROWING'
        // → port không trả về → service không double-mark
        when(loadBorrowSlipPort.findAllActivePastDue(TODAY)).thenReturn(List.of());

        // Act
        int firstRun = service.executeOn(TODAY);
        int secondRun = service.executeOn(TODAY);

        // Assert
        assertEquals(0, firstRun);
        assertEquals(0, secondRun);
        verify(saveBorrowSlipPort, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("execute: Trả về đúng số lượng phiếu đã được cập nhật")
    void execute_ReturnsCorrectCount() {
        // Arrange
        BorrowSlip slip1 = makeBorrowingSlip(1L, YESTERDAY);
        BorrowSlip slip2 = makeBorrowingSlip(2L, YESTERDAY);
        BorrowSlip slip3 = makeBorrowingSlip(3L, TODAY.minusDays(5));
        when(loadBorrowSlipPort.findAllActivePastDue(TODAY)).thenReturn(List.of(slip1, slip2, slip3));
        when(saveBorrowSlipPort.saveAll(anyList())).thenReturn(List.of(slip1, slip2, slip3));

        // Act
        int updated = service.executeOn(TODAY);

        // Assert
        assertEquals(3, updated);
    }
}
