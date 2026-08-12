package org.example.librarymanagement.port.outbound.borrow;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;

public interface LoadBorrowSlipPort {
    PageResult<BorrowSlipResponseDto> findBorrowSlips(
        int page, 
        int size, 
        BorrowSlipStatus status, 
        String keyword
    );
    /**
     * Tìm phiếu mượn theo ID (Hỗ trợ mở rộng cho các Use Case xem chi tiết / trả sách sau này)
     */
    Optional<BorrowSlip> findById(Long id);
    /**
     * Kiểm tra sự tồn tại của mã phiếu mượn (Tránh trùng mã khi tạo mới)
     */
    boolean existsByBorrowCode(String borrowCode);

        /**
     * Lấy thông tin chi tiết phiếu mượn (kèm mã thẻ, tên độc giả, tổng số sách) theo ID
     */
    Optional<BorrowSlipResponseDto> findSlipDetailById(Long id);

}