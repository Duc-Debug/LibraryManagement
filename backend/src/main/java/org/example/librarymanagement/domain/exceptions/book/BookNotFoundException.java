package org.example.librarymanagement.domain.exceptions.book;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class BookNotFoundException extends DomainException {

    public BookNotFoundException(Long bookId) {
        super("Book not found with ID: " + bookId);
    }

    public BookNotFoundException(String message) {
        super(message);
    }
}