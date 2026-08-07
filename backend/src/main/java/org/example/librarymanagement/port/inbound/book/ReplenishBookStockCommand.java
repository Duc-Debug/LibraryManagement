package org.example.librarymanagement.port.inbound.book;

public record ReplenishBookStockCommand(
        Long bookId,
        int quantityToAdd
) {}
