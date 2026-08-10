package org.example.librarymanagement.port.inbound.book;

import java.util.List;

import org.example.librarymanagement.port.dtos.book.BookResult;

public interface CreateBookUseCase {
    
    BookResult createBook(CreateBookCommand command);
    
    // Bắt buộc phải có tham số phân trang để chống tràn RAM (OOM)
    List<BookResult> getAllBooks(int page, int size);
}