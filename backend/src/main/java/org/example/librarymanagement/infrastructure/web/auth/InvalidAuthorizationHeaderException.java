package org.example.librarymanagement.infrastructure.web.auth;

public class InvalidAuthorizationHeaderException extends RuntimeException {

    public InvalidAuthorizationHeaderException(String message) {
        super(message);
    }
}