package org.example.librarymanagement.infrastructure.transaction.borrow;

import org.example.librarymanagement.port.inbound.borrow.UpdateOverdueBorrowSlipsUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalUpdateOverdueBorrowSlipsUseCase implements UpdateOverdueBorrowSlipsUseCase {

    private final UpdateOverdueBorrowSlipsUseCase delegate;

    public TransactionalUpdateOverdueBorrowSlipsUseCase(UpdateOverdueBorrowSlipsUseCase delegate) {
        this.delegate = delegate;
    }

    @Override
    @Transactional
    public int execute() {
        return delegate.execute();
    }
}
