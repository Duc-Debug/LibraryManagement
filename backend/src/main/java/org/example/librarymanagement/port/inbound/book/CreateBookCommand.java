package org.example.librarymanagement.port.inbound.book;



public record CreateBookCommand(
        String title,
        String author,
        String isbn,
        String description,
        String coverImageUrl,
        String publisher,
        Integer publishedYear,
        String shelfLocation,
        int totalQuantity,
        Long categoryId
) {
}