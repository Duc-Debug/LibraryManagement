package org.example.librarymanagement.port.inbound.book;

public interface DeleteBookUseCase {
    void deleteBook(Long bookId);
    void hideBook(Long bookId);
    void unhideBook(Long bookId);
}