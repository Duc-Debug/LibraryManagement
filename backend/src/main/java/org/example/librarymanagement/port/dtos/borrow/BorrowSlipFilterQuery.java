package org.example.librarymanagement.port.dtos.borrow;

import org.example.librarymanagement.domain.enums.BorrowSlipStatus;


public record BorrowSlipFilterQuery(
        int page,
        int size,
        BorrowSlipStatus status,
        String keyword
) {
    public static BorrowSlipFilterQuery of(int page, int size, BorrowSlipStatus status, String keyword) {
        return new BorrowSlipFilterQuery(page, size, status, keyword);
    }
}