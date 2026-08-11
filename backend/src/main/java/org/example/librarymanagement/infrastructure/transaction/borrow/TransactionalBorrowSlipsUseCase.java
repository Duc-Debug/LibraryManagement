package org.example.librarymanagement.infrastructure.transaction.borrow;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for BorrowSlipsUseCase
 * Bọc Spring @Transactional(readOnly = true) ngoài Pure Java GetBorrowSlipsService
 */
public class TransactionalBorrowSlipsUseCase implements BorrowSlipsUseCase {

    private final BorrowSlipsUseCase delegate;

    public TransactionalBorrowSlipsUseCase(BorrowSlipsUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "BorrowSlipsUseCase delegate must not be null");
    }

    @Override
    @Transactional(readOnly = true)
    public PageResult<BorrowSlipResponseDto> getBorrowSlips(BorrowSlipFilterQuery query) {
        return delegate.getBorrowSlips(query);
    }
}
