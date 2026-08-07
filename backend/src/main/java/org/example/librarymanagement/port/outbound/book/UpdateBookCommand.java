package org.example.librarymanagement.port.outbound.book;
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
        Long categoryId
        
) {}


