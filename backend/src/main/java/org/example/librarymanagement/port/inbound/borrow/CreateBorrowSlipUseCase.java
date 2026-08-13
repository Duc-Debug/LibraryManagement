package org.example.librarymanagement.port.inbound.borrow;

import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.CreateBorrowSlipCommand;

public interface CreateBorrowSlipUseCase {
    BorrowSlipResponseDto createBorrowSlip(CreateBorrowSlipCommand command);
}
