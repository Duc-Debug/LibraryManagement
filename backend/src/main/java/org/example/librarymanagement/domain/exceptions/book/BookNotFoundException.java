package org.example.librarymanagement.domain.exceptions.book;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class BookNotFoundException extends DomainException {
    public BookNotFoundException(Long bookId) {
        super("Không tìm thấy sách với ID: " + bookId);
    }
}
