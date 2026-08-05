package org.example.librarymanagement.port.inbound.manage;



public interface UpdateBookUseCase {
    BookResult updateBook(UpdateBookCommand command);
}
