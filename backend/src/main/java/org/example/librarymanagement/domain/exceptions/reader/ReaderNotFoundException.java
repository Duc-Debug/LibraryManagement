package org.example.librarymanagement.domain.exceptions.reader;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class ReaderNotFoundException extends DomainException {

    private ReaderNotFoundException(String message) {
        super(message);
    }

    public static ReaderNotFoundException withId(Long readerId) {
        return new ReaderNotFoundException(
                "Reader not found with id: " + readerId);
    }
}
