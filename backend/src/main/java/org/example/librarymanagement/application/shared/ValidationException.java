package org.example.librarymanagement.application.shared;

import org.example.librarymanagement.application.shared.exception.ApplicationException;

public class ValidationException extends ApplicationException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}