package org.example.librarymanagement.port.inbound.category;

public record UpdateCategoryCommand(
        Long categoryId,
        String name,
        String description,
        Boolean active
) {
}