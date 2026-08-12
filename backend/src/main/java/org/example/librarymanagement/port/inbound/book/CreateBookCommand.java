package org.example.librarymanagement.port.inbound.book;

import java.io.InputStream;

public record CreateBookCommand(
        String title,
        String author,
        String isbn,
        String description,
        InputStream imageInputStream,
        String imageFilename,
        long imageSize,
        String publisher,
        Integer publishedYear,
        String shelfLocation,
        int totalQuantity,
        Long categoryId
) {
}