package org.example.librarymanagement.domain.exceptions.book;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class InvalidBookDataException extends DomainException {
    public InvalidBookDataException(String message) {
        super(message);
    }
}
