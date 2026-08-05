package org.example.librarymanagement.port.inbound.book;

import java.util.UUID;

public interface DeleteBookUseCase {
void deleteBook(UUID bookId);
void hideBook(UUID bookId);    
} 