package org.example.librarymanagement.infrastructure.web.borrow;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController("librarianBorrowSlipManagementController")
@RequestMapping("/api/librarians/borrow-slips")
@RequiredArgsConstructor
@Validated
public class BorrowSlipManagementController {

    private final BorrowSlipsUseCase borrowSlipsUseCase;

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
}