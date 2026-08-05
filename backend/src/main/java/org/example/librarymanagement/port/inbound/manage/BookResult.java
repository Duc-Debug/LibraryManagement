package org.example.librarymanagement.port.inbound.manage;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookResult(
    UUID bookId,
    String title,
    String author,
    String isbn,
    String description,
    String coverImageUrl,
    String publisher,
    Integer publishedYear,
    String shelfLocation,
    int totalQuantity,
    int availableQuantity,
    UUID categoryId,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}