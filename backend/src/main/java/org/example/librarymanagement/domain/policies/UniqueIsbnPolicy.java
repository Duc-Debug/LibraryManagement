package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class UniqueIsbnPolicy {
    public static void validateIsbnForCreate(boolean isIsbnExisted, String isbn) {
        if (isIsbnExisted) {
            throw new DomainException("ISBN'" + isbn + "' is already in use.");
        }
    }

    public static void validateIsbnForUpdate(boolean isIsbnExisted, String isbn) {
        if (isIsbnExisted) {
            throw new DomainException("ISBN'" + isbn + "' is already in use.");
        }
    }
}
