package org.example.librarymanagement.port.inbound.book;

import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.book.UpdateBookCommand;

public interface UpdateBookUseCase {
    BookResult updateBook(UpdateBookCommand command);
}
