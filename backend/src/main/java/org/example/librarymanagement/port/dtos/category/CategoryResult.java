package org.example.librarymanagement.port.dtos.category;

import java.time.LocalDateTime;

public record CategoryResult(
        Long id,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}