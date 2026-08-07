package org.example.librarymanagement.port.inbound.book;

import java.util.List;

import org.example.librarymanagement.domain.entity.Book;


public interface CreateBookUseCase {

    Book createBook(CreateBookCommand command);


    List<Book> getAllBooks();

}