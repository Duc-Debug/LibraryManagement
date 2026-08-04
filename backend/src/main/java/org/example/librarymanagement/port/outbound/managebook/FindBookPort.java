package org.example.librarymanagement.port.outbound.managebook;

import java.util.List;
import org.example.librarymanagement.domain.entity.Book;

public interface FindBookPort {

    boolean existsByIsbn(String isbn);

    List<Book> findAll();

}