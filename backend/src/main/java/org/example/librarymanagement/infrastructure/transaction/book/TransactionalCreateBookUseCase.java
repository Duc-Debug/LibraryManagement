package org.example.librarymanagement.infrastructure.transaction.book;

<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> c3e01c2f6f381e46632fc6b7aed44bc360242854
import java.util.Objects;

import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.springframework.transaction.annotation.Transactional;

<<<<<<< HEAD
=======
/**
 * Transactional Decorator Proxy cho CreateBookUseCase
 * Bọc Spring @Transactional ngoài Pure Java CreateBookService
 */
>>>>>>> c3e01c2f6f381e46632fc6b7aed44bc360242854
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
<<<<<<< HEAD
=======

    @Override
    @Transactional(readOnly = true)
    public List<BookResult> getAllBooks(int page, int size) {
        return delegate.getAllBooks(page, size);
    }
>>>>>>> c3e01c2f6f381e46632fc6b7aed44bc360242854
}
