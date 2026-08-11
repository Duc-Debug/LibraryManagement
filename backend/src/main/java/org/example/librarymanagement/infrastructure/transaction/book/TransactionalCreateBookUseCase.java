package org.example.librarymanagement.infrastructure.transaction.book;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy cho CreateBookUseCase
 * Bọc Spring @Transactional ngoài Pure Java CreateBookService
 */
public class TransactionalCreateBookUseCase implements CreateBookUseCase {

    private final CreateBookUseCase delegate;

    public TransactionalCreateBookUseCase(CreateBookUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "CreateBookUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public BookResult createBook(CreateBookCommand command) {
        return delegate.createBook(command);
    }
}
