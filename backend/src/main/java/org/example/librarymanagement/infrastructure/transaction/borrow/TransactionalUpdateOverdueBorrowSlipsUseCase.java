package org.example.librarymanagement.infrastructure.transaction.borrow;

import java.util.Objects;

import org.example.librarymanagement.port.inbound.borrow.UpdateOverdueBorrowSlipsUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalUpdateOverdueBorrowSlipsUseCase implements UpdateOverdueBorrowSlipsUseCase {

    private final UpdateOverdueBorrowSlipsUseCase delegate;

    public TransactionalUpdateOverdueBorrowSlipsUseCase(UpdateOverdueBorrowSlipsUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "UpdateOverdueBorrowSlipsUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public int updateOverdueSlips() {
        return delegate.updateOverdueSlips();
    }
}
