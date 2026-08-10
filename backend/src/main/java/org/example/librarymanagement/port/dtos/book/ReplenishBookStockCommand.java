package org.example.librarymanagement.port.dtos.book;

public record ReplenishBookStockCommand(
        Long bookId,
        int quantityToAdd
) {}
