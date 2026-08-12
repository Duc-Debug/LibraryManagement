package org.example.librarymanagement.port.inbound.borrow;

import org.example.librarymanagement.port.dtos.borrow.ReturnBorrowSlipResponseDto;

public interface ReturnBorrowSlipUseCase {

    ReturnBorrowSlipResponseDto returnBorrowSlip(Long borrowSlipId);
}