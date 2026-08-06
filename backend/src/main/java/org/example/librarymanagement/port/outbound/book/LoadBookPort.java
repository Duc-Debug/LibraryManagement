package org.example.librarymanagement.port.outbound.book;
import java.util.Optional;
import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.port.inbound.common.PageResult;

public interface LoadBookPort {
    Optional<Book> findById(Long bookId);
    PageResult<Book> findAll(int page, int size, String keyword);
}
