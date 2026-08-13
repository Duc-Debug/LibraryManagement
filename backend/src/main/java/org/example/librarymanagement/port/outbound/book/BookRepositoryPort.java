package org.example.librarymanagement.port.outbound.book;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;

public interface BookRepositoryPort {
    Optional<Book> findById(Long id);
    Optional<Book> findByIdForUpdate(Long id);
    boolean existsById(Long id);
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, Long id);
    Book save(Book book);
    List<Book> saveAll(List<Book> books);
    List<Book> findAll(int page, int size);
}
