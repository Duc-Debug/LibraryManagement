package org.example.librarymanagement.application.managebook;

import java.util.List;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.inbound.managebook.CreateBookCommand;
import org.example.librarymanagement.port.inbound.managebook.ManageBookUseCase;
import org.example.librarymanagement.port.outbound.managebook.FindBookPort;
import org.example.librarymanagement.port.outbound.managebook.SaveBookPort;

import org.springframework.stereotype.Service;


@Service
public class BookManagementService implements ManageBookUseCase {


    private final FindBookPort findBookPort;
    private final SaveBookPort saveBookPort;


    public BookManagementService(
            FindBookPort findBookPort,
            SaveBookPort saveBookPort
    ) {
        this.findBookPort = findBookPort;
        this.saveBookPort = saveBookPort;
    }



   @Override
public Book createBook(CreateBookCommand command) {


    // 1. Validate dữ liệu đầu vào

    if (command.title() == null || command.title().isBlank()) {
        throw new DomainException("Tên sách không được để trống");
    }


    if (command.author() == null || command.author().isBlank()) {
        throw new DomainException("Tác giả không được để trống");
    }


    if (command.isbn() == null || command.isbn().isBlank()) {
        throw new DomainException("ISBN không được để trống");
    }


    if (command.categoryId() == null) {
        throw new DomainException("Thể loại không được để trống");
    }


    if (command.totalQuantity() <= 0) {
        throw new DomainException("Số lượng sách phải lớn hơn 0");
    }


    if (command.coverImageUrl() == null 
            || command.coverImageUrl().isBlank()) {
        throw new DomainException("Ảnh bìa không được để trống");
    }



    // 2. Kiểm tra ISBN trùng

    if(findBookPort.existsByIsbn(command.isbn())) {

        throw new DomainException(
                "ISBN đã tồn tại"
        );

    }



    // 3. Tạo Book domain

    Book book = new Book();

    book.setTitle(command.title());
    book.setAuthor(command.author());
    book.setIsbn(command.isbn());

    book.setDescription(command.description());

    book.setCoverImageUrl(
            command.coverImageUrl()
    );

    book.setPublisher(
            command.publisher()
    );

    book.setPublishedYear(
            command.publishedYear()
    );

    book.setShelfLocation(
            command.shelfLocation()
    );

    book.setCategoryId(
            command.categoryId()
    );


    book.setTotalQuantity(
            command.totalQuantity()
    );


    // ban đầu số lượng có sẵn = tổng số lượng
    book.setAvailableQuantity(
            command.totalQuantity()
    );


    // 4. Save DB

    return saveBookPort.save(book);
}


    @Override
    public List<Book> getAllBooks() {

        return findBookPort.findAll();

    }

}