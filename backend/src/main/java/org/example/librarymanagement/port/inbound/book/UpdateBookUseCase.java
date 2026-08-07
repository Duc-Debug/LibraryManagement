package org.example.librarymanagement.port.inbound.book;



public interface UpdateBookUseCase {
    BookResult updateBook(UpdateBookCommand command);
}
