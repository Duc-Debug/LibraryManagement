package org.example.librarymanagement.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BorrowSlipStatus {
    BORROWING("Borrowing"),
    RETURNED("Returned"),
    OVERDUE("Overdue"),
    LOST("Lost");

    private final String description;
}