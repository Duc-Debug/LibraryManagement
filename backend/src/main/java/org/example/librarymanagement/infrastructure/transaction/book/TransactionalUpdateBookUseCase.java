package org.example.librarymanagement.infrastructure.transaction.book;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.book.UpdateBookCommand;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for UpdateBookUseCase
 * Bọc Spring @Transactional ngoài Pure Java UpdateBookService
 */
public class TransactionalUpdateBookUseCase implements UpdateBookUseCase {

    private final UpdateBookUseCase delegate;

    public TransactionalUpdateBookUseCase(UpdateBookUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "UpdateBookUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public BookResult updateBook(UpdateBookCommand command) {
        return delegate.updateBook(command);
    }
}
