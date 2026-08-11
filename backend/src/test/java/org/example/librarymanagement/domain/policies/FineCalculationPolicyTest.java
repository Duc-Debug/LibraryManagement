package org.example.librarymanagement.domain.policies;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.example.librarymanagement.domain.enums.FineType;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FineCalculationPolicyTest {

    // ==================== 1. OVERDUE FINE TESTS ====================

    @Test
    @DisplayName("calculateOverdueDays: Trả đúng hạn hoặc trước hạn -> 0 ngày quá hạn")
    void calculateOverdueDays_OnTime_ReturnsZero() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 15, 17, 0);
        LocalDateTime onTime = LocalDateTime.of(2026, 8, 15, 9, 0);
        LocalDateTime beforeDue = LocalDateTime.of(2026, 8, 10, 10, 0);

        assertEquals(0, FineCalculationPolicy.calculateOverdueDays(dueDate, onTime));
        assertEquals(0, FineCalculationPolicy.calculateOverdueDays(dueDate, beforeDue));
        assertFalse(FineCalculationPolicy.isOverdue(dueDate, onTime));
    }

    @Test
    @DisplayName("calculateOverdueDays: Trả trễ hạn -> Trả về đúng số ngày chênh lệch")
    void calculateOverdueDays_Overdue_ReturnsDays() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 15, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 18, 15, 30);

        assertEquals(3, FineCalculationPolicy.calculateOverdueDays(dueDate, returnDate));
        assertTrue(FineCalculationPolicy.isOverdue(dueDate, returnDate));
    }

    @Test
    @DisplayName("calculateOverdueFine: Trả đúng hạn -> Tiền phạt = 0 VNĐ")
    void calculateOverdueFine_OnTime_ReturnsZeroAmount() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 15, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 14, 10, 0);

        BigDecimal fine = FineCalculationPolicy.calculateOverdueFine(dueDate, returnDate, 2);
        assertEquals(BigDecimal.ZERO, fine);
    }

    @Test
    @DisplayName("calculateOverdueFine: Quá hạn 3 ngày với 2 cuốn sách (5.000đ/ngày) -> 30.000 VNĐ")
    void calculateOverdueFine_Overdue_ReturnsCalculatedAmount() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 15, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 18, 10, 0);

        BigDecimal fine = FineCalculationPolicy.calculateOverdueFine(dueDate, returnDate, 2);
        assertEquals(BigDecimal.valueOf(30000), fine);
    }

    @Test
    @DisplayName("calculateOverdueFine: Ném DomainException khi bookCount âm")
    void calculateOverdueFine_NegativeBookCount_ThrowsDomainException() {
        LocalDateTime dueDate = LocalDateTime.of(2026, 8, 15, 10, 0);
        LocalDateTime returnDate = LocalDateTime.of(2026, 8, 18, 10, 0);

        DomainException exception = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateOverdueFine(dueDate, returnDate, -1));
        assertEquals("Book count cannot be negative", exception.getMessage());
    }

    // ==================== 2. DAMAGED FINE TESTS ====================

    @Test
    @DisplayName("calculateDamagedBookFine: Tính phạt 50% giá bìa sách (100.000đ -> 50.000đ)")
    void calculateDamagedBookFine_DefaultRate_Success() {
        BigDecimal bookPrice = BigDecimal.valueOf(100000);
        BigDecimal fine = FineCalculationPolicy.calculateDamagedBookFine(bookPrice);

        assertEquals(BigDecimal.valueOf(50000), fine);
    }

    @Test
    @DisplayName("calculateDamagedBookFine: Ném DomainException khi tỷ lệ hỏng không hợp lệ (<0 hoặc >1)")
    void calculateDamagedBookFine_InvalidPercentage_ThrowsDomainException() {
        BigDecimal bookPrice = BigDecimal.valueOf(100000);

        DomainException exLow = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateDamagedBookFine(bookPrice, -0.1));
        assertEquals("Damage percentage must be between 0.0 (0%) and 1.0 (100%)", exLow.getMessage());

        DomainException exHigh = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateDamagedBookFine(bookPrice, 1.5));
        assertEquals("Damage percentage must be between 0.0 (0%) and 1.0 (100%)", exHigh.getMessage());
    }

    @Test
    @DisplayName("calculateDamagedBookFine: Ném DomainException khi giá sách null hoặc <= 0")
    void calculateDamagedBookFine_InvalidPrice_ThrowsDomainException() {
        DomainException exNull = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateDamagedBookFine(null, 0.5));
        assertEquals("Book price must be greater than 0", exNull.getMessage());

        DomainException exZero = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateDamagedBookFine(BigDecimal.ZERO, 0.5));
        assertEquals("Book price must be greater than 0", exZero.getMessage());

        DomainException exNeg = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateDamagedBookFine(BigDecimal.valueOf(-1000), 0.5));
        assertEquals("Book price must be greater than 0", exNeg.getMessage());
    }

    // ==================== 3. LOST FINE TESTS ====================

    @Test
    @DisplayName("calculateLostBookFine: Bồi thường 100% giá sách + 20.000đ phí xử lý")
    void calculateLostBookFine_DefaultRate_Success() {
        BigDecimal bookPrice = BigDecimal.valueOf(100000);
        BigDecimal fine = FineCalculationPolicy.calculateLostBookFine(bookPrice);

        // 100.000 + 20.000 = 120.000đ
        assertEquals(BigDecimal.valueOf(120000), fine);
    }

    @Test
    @DisplayName("calculateLostBookFine: Ném DomainException khi giá sách null hoặc <= 0")
    void calculateLostBookFine_InvalidPrice_ThrowsDomainException() {
        DomainException exNull = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateLostBookFine(null));
        assertEquals("Book price must be greater than 0", exNull.getMessage());

        DomainException exZero = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateLostBookFine(BigDecimal.ZERO));
        assertEquals("Book price must be greater than 0", exZero.getMessage());
    }

    @Test
    @DisplayName("calculateLostBookFine: Ném DomainException khi phí xử lý âm")
    void calculateLostBookFine_NegativeProcessingFee_ThrowsDomainException() {
        BigDecimal bookPrice = BigDecimal.valueOf(100000);
        DomainException exception = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateLostBookFine(bookPrice, 1.0, BigDecimal.valueOf(-5000)));
        assertEquals("Processing fee cannot be negative", exception.getMessage());
    }

    // ==================== 4. MISSING ACCESSORY TESTS ====================

    @Test
    @DisplayName("calculateMissingAccessoryFine: Phạt mất phụ kiện mặc định 30.000đ")
    void calculateMissingAccessoryFine_Default_Success() {
        BigDecimal fine = FineCalculationPolicy.calculateMissingAccessoryFine(null);
        assertEquals(BigDecimal.valueOf(30000), fine);
    }

    // ==================== 5. COMBINED TOTAL FINE TESTS ====================

    @Test
    @DisplayName("calculateTotalFine: Tổng hợp các khoản phạt chính xác")
    void calculateTotalFine_Combined_Success() {
        BigDecimal overdue = BigDecimal.valueOf(15000);
        BigDecimal damaged = BigDecimal.valueOf(50000);

        BigDecimal total = FineCalculationPolicy.calculateTotalFine(overdue, damaged, null, null, null);
        assertEquals(BigDecimal.valueOf(65000), total);
    }

    @Test
    @DisplayName("calculateTotalFine: Ném DomainException khi có khoản phạt âm")
    void calculateTotalFine_NegativeComponent_ThrowsDomainException() {
        DomainException exception = assertThrows(DomainException.class, () ->
                FineCalculationPolicy.calculateTotalFine(BigDecimal.valueOf(-1000), null, null, null, null));
        assertEquals("Overdue fine cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("formatFineReason: Định dạng chuỗi lý do có tiền tố loại phạt")
    void formatFineReason_Success() {
        String reason = FineCalculationPolicy.formatFineReason(FineType.OVERDUE, "Quá hạn 3 ngày");
        assertEquals("[Trả quá hạn] Quá hạn 3 ngày", reason);
    }
}
