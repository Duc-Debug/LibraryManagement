package org.example.librarymanagement.domain.exceptions.shared;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class AccessDeniedException extends DomainException {

    public AccessDeniedException(String message) {
        super(message);
    }

    public static AccessDeniedException defaultMessage() {
        return new AccessDeniedException("Access denied: You do not have permission to perform this action.");
    }
}