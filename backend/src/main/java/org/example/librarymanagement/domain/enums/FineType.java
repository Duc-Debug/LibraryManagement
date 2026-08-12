package org.example.librarymanagement.domain.enums;

public enum FineType {
    OVERDUE("Trả quá hạn"),
    DAMAGED("Hỏng / rách sách"),
    LOST("Làm mất sách"),
    MISSING_ACCESSORY("Mất phụ kiện kèm theo"),
    OTHER("Lý do khác");

    private final String description;

    FineType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}