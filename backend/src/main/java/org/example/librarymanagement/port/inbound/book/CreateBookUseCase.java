package org.example.librarymanagement.port.inbound.book;

import org.example.librarymanagement.port.dtos.book.BookResult;

public interface CreateBookUseCase {
    BookResult createBook(CreateBookCommand command);
}