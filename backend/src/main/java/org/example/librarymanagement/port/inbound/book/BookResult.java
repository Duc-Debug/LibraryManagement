package org.example.librarymanagement.port.inbound.book;

public record BookResult(
        Long id,
        String title,
        String author,
        String isbn,
        String coverImageUrl,
        int totalQuantity,
        int availableQuantity,
        Long categoryId,
        boolean active
) {
}