package org.example.librarymanagement.infrastructure.transaction.book;

import java.util.Objects;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockCommand;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalReplenishBookStockUseCase implements ReplenishBookStockUseCase {

    private final ReplenishBookStockUseCase delegate;

    public TransactionalReplenishBookStockUseCase(ReplenishBookStockUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "ReplenishBookStockUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public BookResult replenishStock(ReplenishBookStockCommand command) {
        return delegate.replenishStock(command);
    }
}
