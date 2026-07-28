package org.example.librarymanagement.domain.exceptions;

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}