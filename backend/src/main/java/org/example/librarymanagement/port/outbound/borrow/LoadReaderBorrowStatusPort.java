package org.example.librarymanagement.port.outbound.borrow;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.port.dtos.borrow.OverdueSlipSummaryDto;

public interface LoadReaderBorrowStatusPort {

    /**
     * Đếm tổng số cuốn sách bạn đọc đang mượn chưa trả
     * (phiếu mượn có status IN ('BORROWING', 'OVERDUE') và chi tiết sách có returned_at IS NULL)
     *
     * @param readerId ID độc giả
     * @return Số lượng sách chưa trả
     */
    int countCurrentBorrowingBooks(Long readerId);

    /**
     * Lấy danh sách các phiếu mượn có sách quá hạn chưa trả
     *
     * @param readerId ID độc giả
     * @param asOfDate Thời điểm tính quá hạn (thường là LocalDateTime.now())
     * @return Danh sách tóm tắt các phiếu quá hạn
     */
    List<OverdueSlipSummaryDto> findOverdueSlipsByReaderId(Long readerId, LocalDateTime asOfDate);

    /**
     * Kiểm tra nhanh xem độc giả có cuốn sách nào quá hạn chưa trả hay không
     *
     * @param readerId ID độc giả
     * @param asOfDate Thời điểm tính quá hạn
     * @return true nếu có sách quá hạn chưa trả, ngược lại false
     */
    boolean hasOverdueBooks(Long readerId, LocalDateTime asOfDate);
}
