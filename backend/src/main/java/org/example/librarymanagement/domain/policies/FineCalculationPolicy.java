package org.example.librarymanagement.domain.policies;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.example.librarymanagement.domain.enums.FineType;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class FineCalculationPolicy {

    /** 1. Đơn giá phạt trả quá hạn mặc định: 5.000 VNĐ / ngày / cuốn */
    public static final BigDecimal DEFAULT_DAILY_OVERDUE_RATE = BigDecimal.valueOf(5000);
    /** 2. Tỷ lệ phạt hỏng sách mặc định: 50% giá bìa sách */
    public static final double DEFAULT_DAMAGED_PERCENTAGE = 0.50;
    /** 3. Tỷ lệ bồi thường mất sách mặc định: 100% giá bìa sách */
    public static final double DEFAULT_LOST_COMPENSATION_RATE = 1.00;
    /** Phí xử lý nghiệp vụ khi mất sách (Processing fee): 20.000 VNĐ */
    public static final BigDecimal DEFAULT_LOST_PROCESSING_FEE = BigDecimal.valueOf(20000);
    /** 4. Phí phạt mất phụ kiện mặc định (Đĩa CD/DVD/Bản đồ): 30.000 VNĐ */
    public static final BigDecimal DEFAULT_MISSING_ACCESSORY_FINE = BigDecimal.valueOf(30000);
    // ======================================================================
    // 1. PHÍ PHẠT TRẢ QUÁ HẠN (OVERDUE FINE)
    // ======================================================================
    /**
     * Tính số ngày quá hạn giữa Hạn trả và Ngày trả thực tế
     */
    public static long calculateOverdueDays(LocalDateTime dueDate, LocalDateTime returnDate) {
        if (dueDate == null || returnDate == null) {
            return 0;
        }
        long daysBetween = ChronoUnit.DAYS.between(dueDate.toLocalDate(), returnDate.toLocalDate());
        return Math.max(0, daysBetween);
    }
    public static boolean isOverdue(LocalDateTime dueDate, LocalDateTime returnDate) {
        return calculateOverdueDays(dueDate, returnDate) > 0;
    }
    /**
     * Tính tiền phạt quá hạn theo số ngày, số lượng sách và đơn giá/ngày
     */
    public static BigDecimal calculateOverdueFine(
            LocalDateTime dueDate,
            LocalDateTime returnDate,
            int bookCount,
            BigDecimal dailyRate) {
        if (bookCount < 0) {
            throw new DomainException("Book count cannot be negative");
        }
        if (dailyRate != null && dailyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Daily fine rate cannot be negative");
        }
        long overdueDays = calculateOverdueDays(dueDate, returnDate);
        if (overdueDays <= 0 || bookCount == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal effectiveRate = (dailyRate != null) ? dailyRate : DEFAULT_DAILY_OVERDUE_RATE;
        return effectiveRate
                .multiply(BigDecimal.valueOf(overdueDays))
                .multiply(BigDecimal.valueOf(bookCount));
    }
    public static BigDecimal calculateOverdueFine(LocalDateTime dueDate, LocalDateTime returnDate, int bookCount) {
        return calculateOverdueFine(dueDate, returnDate, bookCount, DEFAULT_DAILY_OVERDUE_RATE);
    }
    // ======================================================================
    // 2. PHÍ BỒI THƯỜNG HỎNG / RÁCH SÁCH (DAMAGED FINE - FAIL-FAST)
    // ======================================================================
    /**
     * Tính tiền phạt hỏng sách theo % giá trị sách (damagePercentage: 0.0 -> 1.0)
     */
    public static BigDecimal calculateDamagedBookFine(BigDecimal bookPrice, double damagePercentage) {
        if (bookPrice == null || bookPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Book price must be greater than 0");
        }
        if (damagePercentage < 0.0 || damagePercentage > 1.0) {
            throw new DomainException("Damage percentage must be between 0.0 (0%) and 1.0 (100%)");
        }
        return bookPrice.multiply(BigDecimal.valueOf(damagePercentage)).setScale(0, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateDamagedBookFine(BigDecimal bookPrice) {
        return calculateDamagedBookFine(bookPrice, DEFAULT_DAMAGED_PERCENTAGE);
    }

    // ======================================================================
    // 3. PHÍ BỒI THƯỜNG MẤT SÁCH (LOST BOOK FINE - FAIL-FAST)
    // ======================================================================
    /**
     * Tính tiền bồi thường mất sách = (Giá sách * Tỷ lệ đền bù) + Phí xử lý nghiệp vụ
     */
    public static BigDecimal calculateLostBookFine(
            BigDecimal bookPrice,
            double compensationRate,
            BigDecimal processingFee) {

        if (bookPrice == null || bookPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Book price must be greater than 0");
        }
        if (compensationRate < 1.0) {
            throw new DomainException("Compensation rate for lost book must be at least 1.0 (100%)");
        }
        if (processingFee != null && processingFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Processing fee cannot be negative");
        }

        BigDecimal effectiveProcessingFee = (processingFee != null)
                ? processingFee
                : DEFAULT_LOST_PROCESSING_FEE;

        BigDecimal compensationAmount = bookPrice.multiply(BigDecimal.valueOf(compensationRate));
        return compensationAmount.add(effectiveProcessingFee).setScale(0, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateLostBookFine(BigDecimal bookPrice) {
        return calculateLostBookFine(bookPrice, DEFAULT_LOST_COMPENSATION_RATE, DEFAULT_LOST_PROCESSING_FEE);
    }

    // ======================================================================
    // 4. PHÍ MẤT PHỤ KIỆN ĐÍNH KÈM (MISSING ACCESSORY FINE)
    // ======================================================================
    /**
     * Phạt mất phụ kiện (Đĩa CD, tài liệu đính kèm...)
     */
    public static BigDecimal calculateMissingAccessoryFine(BigDecimal customAccessoryFine) {
        if (customAccessoryFine != null) {
            if (customAccessoryFine.compareTo(BigDecimal.ZERO) < 0) {
                throw new DomainException("Accessory fine cannot be negative");
            }
            return customAccessoryFine;
        }
        return DEFAULT_MISSING_ACCESSORY_FINE;
    }

    // ======================================================================
    // 5. PHÍ KHÁC & TÍNH TỔNG PHÍ KẾT HỢP (COMBINED FINE - FAIL-FAST)
    // ======================================================================
    /**
     * Tính tổng các khoản phạt kết hợp cho một lượt trả sách
     */
    public static BigDecimal calculateTotalFine(
            BigDecimal overdueFine,
            BigDecimal damagedFine,
            BigDecimal lostFine,
            BigDecimal accessoryFine,
            BigDecimal otherFine) {

        validateNonNegativeFine("Overdue fine", overdueFine);
        validateNonNegativeFine("Damaged fine", damagedFine);
        validateNonNegativeFine("Lost fine", lostFine);
        validateNonNegativeFine("Accessory fine", accessoryFine);
        validateNonNegativeFine("Other fine", otherFine);

        BigDecimal total = BigDecimal.ZERO;
        if (overdueFine != null) total = total.add(overdueFine);
        if (damagedFine != null) total = total.add(damagedFine);
        if (lostFine != null) total = total.add(lostFine);
        if (accessoryFine != null) total = total.add(accessoryFine);
        if (otherFine != null) total = total.add(otherFine);
        return total;
    }

    private static void validateNonNegativeFine(String fieldName, BigDecimal amount) {
        if (amount != null && amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException(fieldName + " cannot be negative");
        }
    }

    /**
     * Định dạng chuỗi lý do phạt chi tiết theo loại
     */
    public static String formatFineReason(FineType type, String detail) {
        if (type == null) {
            return detail;
        }
        if (detail == null || detail.isBlank()) {
            return type.getDescription();
        }
        return String.format("[%s] %s", type.getDescription(), detail.trim());
    }
}
