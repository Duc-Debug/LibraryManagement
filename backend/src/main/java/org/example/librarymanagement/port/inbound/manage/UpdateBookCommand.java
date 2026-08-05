package org.example.librarymanagement.port.inbound.manage;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateBookCommand(
       @NotNull(message = "Title cannot be null")
       UUID bookId,
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
        Integer publishedYear,

        String shelfLocation,
        @Min(value = 0, message = "Total quantity cannot be negative")
        int totalQuantity,
        @NotNull(message = "Category ID cannot be null")
        UUID categoryId
        
) {}


