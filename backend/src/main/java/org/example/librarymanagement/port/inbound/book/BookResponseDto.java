package org.example.librarymanagement.port.inbound.book;

import java.time.LocalDateTime;

public record BookResponseDto(
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
    String categoryName,
    boolean active,
    LocalDateTime createdAt
) {}