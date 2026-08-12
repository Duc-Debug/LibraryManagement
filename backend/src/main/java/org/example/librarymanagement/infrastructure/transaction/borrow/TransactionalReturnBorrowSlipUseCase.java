package org.example.librarymanagement.infrastructure.transaction.borrow;

import org.example.librarymanagement.port.dtos.borrow.ReturnBorrowSlipResponseDto;
import org.example.librarymanagement.port.inbound.borrow.ReturnBorrowSlipUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalReturnBorrowSlipUseCase
        implements ReturnBorrowSlipUseCase {

    private final ReturnBorrowSlipUseCase delegate;

    public TransactionalReturnBorrowSlipUseCase(
            ReturnBorrowSlipUseCase delegate
    ) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public ReturnBorrowSlipResponseDto returnBorrowSlip(Long borrowSlipId) {
        return delegate.returnBorrowSlip(borrowSlipId);
    }
}