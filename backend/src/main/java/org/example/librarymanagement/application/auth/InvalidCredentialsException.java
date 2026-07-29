package org.example.librarymanagement.application.auth;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Username or password is incorrect");
    }
}
