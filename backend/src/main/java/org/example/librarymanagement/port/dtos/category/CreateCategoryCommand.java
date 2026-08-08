package org.example.librarymanagement.port.dtos.category;

public record CreateCategoryCommand(
        String name,
        String description
) {
}