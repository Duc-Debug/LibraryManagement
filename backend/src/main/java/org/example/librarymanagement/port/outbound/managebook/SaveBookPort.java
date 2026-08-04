package org.example.librarymanagement.port.outbound.managebook;

import org.example.librarymanagement.domain.entity.Book;

public interface SaveBookPort {

    Book save(Book book);

}