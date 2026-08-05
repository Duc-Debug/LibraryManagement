package org.example.librarymanagement.port.inbound.category;

public record CreateCategoryCommand(
        String name,
        String description
) {
}
