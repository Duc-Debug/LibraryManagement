package org.example.librarymanagement.domain.exceptions.user;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}
