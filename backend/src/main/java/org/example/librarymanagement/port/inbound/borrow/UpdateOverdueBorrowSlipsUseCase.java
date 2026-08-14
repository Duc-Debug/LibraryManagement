package org.example.librarymanagement.port.inbound.borrow;

/**
 * Use case: Quét tất cả phiếu mượn đã quá hạn (BORROWING + dueAt < hôm nay)
 * và cập nhật trạng thái sang OVERDUE.
 * Thường được gọi bởi scheduler chạy vào đầu mỗi ngày.
 */
public interface UpdateOverdueBorrowSlipsUseCase {

    /**
     * Thực thi quét và cập nhật. Trả về số lượng phiếu đã được cập nhật.
     */
    int execute();
}
