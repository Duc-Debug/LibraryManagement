package org.example.librarymanagement.port.inbound.borrow;

import java.time.LocalDateTime;

import org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto;

/**
 * Inbound Port: Tính toán và xem trước các loại phí phạt của phiếu mượn
 */
public interface CalculateFineUseCase {

    /**
     * Tính phí phạt cho một phiếu mượn (quá hạn, bồi thường...)
     *
     * @param borrowSlipId ID phiếu mượn
     * @param returnDate Ngày trả tính toán (nếu null -> lấy thời điểm hiện tại)
     * @return DTO chứa đầy đủ thông tin số ngày trễ, đơn giá và tổng tiền phạt
     */
    FineCalculationResponseDto calculateBorrowSlipFine(Long borrowSlipId, LocalDateTime returnDate);
}