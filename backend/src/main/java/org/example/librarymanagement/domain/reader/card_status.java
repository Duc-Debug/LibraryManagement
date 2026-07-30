package org.example.librarymanagement.domain.reader;

public enum card_status {
    ACTIVE("Active"), LOCKED("Locked"), EXPIRED("Expired");

    private final String description;

    card_status(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;

    }
}
