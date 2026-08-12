package org.example.librarymanagement.infrastructure.transaction.borrow;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.borrow.ReaderBorrowEligibilityDto;
import org.example.librarymanagement.port.inbound.borrow.CheckBorrowEligibilityUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for CheckBorrowEligibilityUseCase
 * Bọc Spring @Transactional(readOnly = true) ngoài Pure Java CheckBorrowEligibilityService
 */
public class TransactionalCheckBorrowEligibilityUseCase implements CheckBorrowEligibilityUseCase {

    private final CheckBorrowEligibilityUseCase delegate;

    public TransactionalCheckBorrowEligibilityUseCase(CheckBorrowEligibilityUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "CheckBorrowEligibilityUseCase delegate must not be null");
    }

    @Override
    @Transactional(readOnly = true)
    public ReaderBorrowEligibilityDto checkEligibility(Long readerId) {
        return delegate.checkEligibility(readerId);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateBorrowEligibility(Long readerId, int requestedBooksCount) {
        delegate.validateBorrowEligibility(readerId, requestedBooksCount);
    }
}
