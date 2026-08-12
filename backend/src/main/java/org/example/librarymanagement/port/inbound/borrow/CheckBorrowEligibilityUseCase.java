package org.example.librarymanagement.port.inbound.borrow;

import org.example.librarymanagement.port.dtos.borrow.ReaderBorrowEligibilityDto;

public interface CheckBorrowEligibilityUseCase {

    /**
     * Kiểm tra tổng thể điều kiện mượn sách của độc giả (được dùng để hiển thị trên UI)
     *
     * @param readerId ID độc giả
     * @return DTO chứa thông tin chi tiết về hạn mức, sách quá hạn và kết quả hợp lệ hay không
     */
    ReaderBorrowEligibilityDto checkEligibility(Long readerId);

    /**
     * Kiểm tra và ném ngoại lệ nghiêm ngặt (dùng trong luồng tạo phiếu mượn mới)
     *
     * @param readerId ID độc giả
     * @param requestedBooksCount Số lượng sách yêu cầu mượn
     */
    void validateBorrowEligibility(Long readerId, int requestedBooksCount);
}
