package org.example.librarymanagement.domain.exceptions.shared;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class UnauthenticatedException extends DomainException {

    public UnauthenticatedException(String message) {
        super(message);
    }

    public static UnauthenticatedException defaultMessage() {
        return new UnauthenticatedException("User is unauthenticated or session has expired.");
    }
}