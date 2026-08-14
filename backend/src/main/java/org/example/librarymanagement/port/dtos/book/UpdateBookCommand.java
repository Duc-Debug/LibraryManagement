package org.example.librarymanagement.port.dtos.book;

import java.io.InputStream;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateBookCommand(
        Long bookId,
        @NotNull(message = "Title cannot be null")
        String title,
        @NotNull(message = "Author cannot be null")
        String author,
        @NotNull(message = "ISBN cannot be null")
        String isbn,
        String description,
        String coverImageUrl,
        String publisher,
        @NotNull(message = "Published year cannot be null")
        Short publishedYear,
        String shelfLocation,
        @Min(value = 0, message = "Total quantity cannot be negative")
        int totalQuantity,
        @NotNull(message = "Category ID cannot be null")
        Long categoryId,
        InputStream imageStream,
        String originalFilename,
        long size
) {
    public UpdateBookCommand(
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
            Long categoryId
    ) {
        this(bookId, title, author, isbn, description, coverImageUrl, publisher,
             publishedYear, shelfLocation, totalQuantity, categoryId, null, null, 0L);
    }
}


