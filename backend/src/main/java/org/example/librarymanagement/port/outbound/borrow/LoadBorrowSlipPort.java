package org.example.librarymanagement.port.outbound.borrow;

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
}