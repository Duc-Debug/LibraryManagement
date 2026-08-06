package org.example.librarymanagement.port.outbound.book;

import java.util.Optional;
import org.example.librarymanagement.domain.entity.Book;

public interface LoadBookPort {
    Optional<Book> findById(Long bookId);
    Optional<Book> findAll(int page, int size, String keyword);
}
