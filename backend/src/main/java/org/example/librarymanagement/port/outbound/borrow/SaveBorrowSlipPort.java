package org.example.librarymanagement.port.outbound.borrow;

import java.util.List;

import org.example.librarymanagement.domain.entity.BorrowSlip;

public interface SaveBorrowSlipPort {

    BorrowSlip save(BorrowSlip borrowSlip);

    /**
     * Lưu hàng loạt phiếu mượn. Dùng bởi scheduler khi cập nhật trạng thái OVERDUE.
     */
    List<BorrowSlip> saveAll(List<BorrowSlip> borrowSlips);
}