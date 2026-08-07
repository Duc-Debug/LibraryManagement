package org.example.librarymanagement.port.inbound.book;

public interface ReplenishBookStockUseCase {
    BookResult replenishStock(ReplenishBookStockCommand command);
}
