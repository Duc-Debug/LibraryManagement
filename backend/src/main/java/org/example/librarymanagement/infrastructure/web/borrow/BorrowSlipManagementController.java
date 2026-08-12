package org.example.librarymanagement.infrastructure.web.borrow;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController("librarianBorrowSlipManagementController")
@RequestMapping("/api/librarians/borrow-slips")
@RequiredArgsConstructor
@Validated
public class BorrowSlipManagementController {

    private final BorrowSlipsUseCase borrowSlipsUseCase;
    private final CalculateFineUseCase calculateFineUseCase;

    /**
     * API: Lấy danh sách phiếu mượn phân trang & lọc theo trạng thái
     *
     * @param page    Số trang (bắt đầu từ 0, mặc định 0)
     * @param size    Số phần tử mỗi trang (mặc định 10, tối đa 100)
     * @param status  Trạng thái phiếu mượn (BORROWING, RETURNED, OVERDUE - Tùy chọn)
     * @param keyword Từ khóa tìm kiếm theo mã phiếu, tên độc giả, mã thẻ (Tùy chọn)
     * @return Danh sách phiếu mượn phân trang
     */
    @GetMapping
    public ResponseEntity<PageResult<BorrowSlipResponseDto>> getBorrowSlips(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be greater than or equal to 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be greater than 0") @Max(value = 100, message = "Size must not exceed 100") int size,
            @RequestParam(required = false) BorrowSlipStatus status,
            @RequestParam(required = false) String keyword
    ) {
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(page, size, status, keyword);
        PageResult<BorrowSlipResponseDto> result = borrowSlipsUseCase.getBorrowSlips(query);
        return ResponseEntity.ok(result);
    }

    /**
     * API: Tính toán và xem trước phí phạt khi trả sách (hoặc trả trễ hạn)
     *
     * @param id         ID của phiếu mượn
     * @param returnDate Ngày trả tính toán (Tùy chọn, ISO-8601, ví dụ: 2026-08-15T10:00:00)
     * @return DTO chi tiết phí phạt và số ngày trễ
     */
    @GetMapping("/{id}/fine-preview")
    public ResponseEntity<FineCalculationResponseDto> previewFine(
            @PathVariable @Min(value = 1, message = "Borrow slip ID must be greater than 0") Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime returnDate
    ) {
        FineCalculationResponseDto result = calculateFineUseCase.calculateBorrowSlipFine(id, returnDate);
        return ResponseEntity.ok(result);
    }
}