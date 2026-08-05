package org.example.librarymanagement.port.outbound.book;

import java.util.UUID;

import org.example.librarymanagement.domain.entity.Book;

public interface SaveBookPort {
    Book save(Book book);
    void deleteById(UUID bookId);
}
