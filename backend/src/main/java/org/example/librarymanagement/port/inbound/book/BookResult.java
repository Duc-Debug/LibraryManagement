package org.example.librarymanagement.port.inbound.book;

import java.time.LocalDateTime;

public record BookResult(
    Long bookId,
    String title,
    String author,
    String isbn,
    String description,
    String coverImageUrl,
    String publisher,
    Short publishedYear,
    String shelfLocation,
    int totalQuantity,
    int availableQuantity,
    Long categoryId,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
