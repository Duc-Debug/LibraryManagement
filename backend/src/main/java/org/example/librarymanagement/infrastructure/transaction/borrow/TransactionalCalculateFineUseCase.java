package org.example.librarymanagement.infrastructure.transaction.borrow;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.port.dtos.borrow.FineCalculationResponseDto;
import org.example.librarymanagement.port.inbound.borrow.CalculateFineUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for CalculateFineUseCase
 * Bọc Spring @Transactional(readOnly = true) ngoài Pure Java CalculateFineService
 */
public class TransactionalCalculateFineUseCase implements CalculateFineUseCase {

    private final CalculateFineUseCase delegate;

    public TransactionalCalculateFineUseCase(CalculateFineUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "CalculateFineUseCase delegate must not be null");
    }

    @Override
    @Transactional(readOnly = true)
    public FineCalculationResponseDto calculateBorrowSlipFine(Long borrowSlipId, LocalDateTime returnDate) {
        return delegate.calculateBorrowSlipFine(borrowSlipId, returnDate);
    }
}
