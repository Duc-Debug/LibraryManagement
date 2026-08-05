package org.example.librarymanagement.port.outbound.book;

import java.util.Optional;
import java.util.UUID;

import org.example.librarymanagement.domain.entity.Book;

public interface LoadBookPort {
    Optional<Book> findById(UUID bookId);
}
