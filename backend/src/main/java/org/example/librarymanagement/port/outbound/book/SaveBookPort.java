package org.example.librarymanagement.port.outbound.book;

import org.example.librarymanagement.domain.entity.Book;

import java.util.List;

public interface SaveBookPort {
    Book save(Book book);
    List<Book> saveAll(List<Book> books);
    void deleteById(Long bookId);
}
