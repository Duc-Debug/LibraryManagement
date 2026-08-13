package org.example.librarymanagement.domain.exceptions.report;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class InvalidDateRangeException extends DomainException {

    public InvalidDateRangeException(String message) {
        super(message);
    }
}
