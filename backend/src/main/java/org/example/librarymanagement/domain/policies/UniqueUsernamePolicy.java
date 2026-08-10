package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class UniqueUsernamePolicy {

    public static void validateUsernameForCreate(boolean isUsernameExisted, String username) {
        if (isUsernameExisted) {
            throw new DomainException("Username '" + username + "' already exists in the system.");
        }
    }

    public static void validateUsernameForUpdate(String currentUsername, String newUsername,
            boolean isNewUsernameExisted) {
        if (currentUsername != null && !currentUsername.equalsIgnoreCase(newUsername) && isNewUsernameExisted) {
            throw new DomainException("New username '" + newUsername + "' is already in use by another account.");
        }
    }
}