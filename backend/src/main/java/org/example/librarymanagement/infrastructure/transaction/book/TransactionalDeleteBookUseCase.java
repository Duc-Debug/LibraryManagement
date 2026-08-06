package org.example.librarymanagement.infrastructure.transaction.book;

import java.util.Objects;

import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalDeleteBookUseCase implements DeleteBookUseCase {

    private final DeleteBookUseCase delegate;

    public TransactionalDeleteBookUseCase(DeleteBookUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "DeleteBookUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        delegate.deleteBook(bookId);
    }

    @Override
    @Transactional
    public void hideBook(Long bookId) {
        delegate.hideBook(bookId);
    }

    @Override
    @Transactional
    public void unhideBook(Long bookId) {
        delegate.unhideBook(bookId);
    }
}
