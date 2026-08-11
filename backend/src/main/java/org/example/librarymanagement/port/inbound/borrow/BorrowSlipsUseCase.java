package org.example.librarymanagement.port.inbound.borrow;

import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;

public interface BorrowSlipsUseCase {
    PageResult<BorrowSlipResponseDto> getBorrowSlips(BorrowSlipFilterQuery query);
}
