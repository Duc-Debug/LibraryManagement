package org.example.librarymanagement.port.outbound.book;

public interface UpdateBookUseCase {
    BookResult updateBook(UpdateBookCommand command);
}
