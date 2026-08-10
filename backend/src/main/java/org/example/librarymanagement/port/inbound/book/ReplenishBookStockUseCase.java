package org.example.librarymanagement.port.inbound.book;

import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.book.ReplenishBookStockCommand;

public interface ReplenishBookStockUseCase {
    BookResult replenishStock(ReplenishBookStockCommand command);
}
