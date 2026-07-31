package org.example.librarymanagement.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CardStatus {
    ACTIVE("Active"),
    LOCKED("Locked"),
    EXPIRED("Expired");

    private final String description;
}