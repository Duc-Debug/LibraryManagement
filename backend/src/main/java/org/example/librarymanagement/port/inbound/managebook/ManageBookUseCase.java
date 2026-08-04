package org.example.librarymanagement.port.inbound.managebook;

import java.util.List;

import org.example.librarymanagement.domain.entity.Book;


public interface ManageBookUseCase {

    Book createBook(CreateBookCommand command);


    List<Book> getAllBooks();

}