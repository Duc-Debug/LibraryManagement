package org.example.librarymanagement.domain.policies;

public class UniqueIsbnPolicy {
    public void validateIsbnForCreate(boolean isIsbnExisted, String isbn) {
        if (isIsbnExisted) {
            throw new IllegalArgumentException("ISBN'" + isbn + "' is already in use.");
        }
    }

    public void validateIsbnForUpdate(boolean isIsbnExisted, String isbn) {
        if (isIsbnExisted) {
            throw new IllegalArgumentException("ISBN'" + isbn + "' is already in use.");
        }
    }
}
