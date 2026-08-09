package org.example.librarymanagement.domain.exceptions.shared;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Username or password is incorrect");
    }
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
