package org.example.librarymanagement.infrastructure.transaction.borrow;

import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.borrow.CreateBorrowSlipCommand;
import org.example.librarymanagement.port.inbound.borrow.CreateBorrowSlipUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalCreateBorrowSlipUseCase implements CreateBorrowSlipUseCase{

    private final CreateBorrowSlipUseCase delegate;
    public TransactionalCreateBorrowSlipUseCase(CreateBorrowSlipUseCase delegate){
        this.delegate = delegate;
    }
    @Override
    @Transactional
    public BorrowSlipResponseDto createBorrowSlip(CreateBorrowSlipCommand command) {
       return delegate.createBorrowSlip(command);
    }
    
}
