package org.example.librarymanagement.port.dtos.category;

public record UpdateCategoryCommand(
        Long categoryId,
        String name,
        String description,
        Boolean active
) {
}