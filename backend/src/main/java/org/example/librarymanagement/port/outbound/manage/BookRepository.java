package org.example.librarymanagement.port.outbound.manage;

import java.util.Optional;
import java.util.UUID;

import org.example.librarymanagement.domain.entity.Book;

public interface BookRepository {
    Optional<Book> findById(UUID id);
    boolean existsById(String id);
    boolean existsByIsbnAndIdNot(String isbn, UUID id);
    Book save(Book book);
}
