package org.example.librarymanagement.domain.enums;

public enum CardStatus {
    ACTIVE("Active"), LOCKED("Locked"), EXPIRED("Expired");

    private final String description;

    CardStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;

    }
}
