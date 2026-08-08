package org.example.librarymanagement.port.outbound.book;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;

public interface BookRepositoryPort {
    Optional<Book> findById(Long id);
    Optional<Book> findByIdForUpdate(Long id);
    boolean existsById(Long id);
    boolean existsByIsbnAndIdNot(String isbn, Long id);
    Book save(Book book);
}
