package org.example.librarymanagement.application.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.example.librarymanagement.application.managebook.BookManagementService;
import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.inbound.managebook.CreateBookCommand;
import org.example.librarymanagement.port.outbound.managebook.FindBookPort;
import org.example.librarymanagement.port.outbound.managebook.SaveBookPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class BookManagementServiceTest {


    @Mock
    private FindBookPort findBookPort;


    @Mock
    private SaveBookPort saveBookPort;


    @InjectMocks
    private BookManagementService bookManagementService;


    private CreateBookCommand command;


    @BeforeEach
    void setUp() {

        command = new CreateBookCommand(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Software book",
                "image.jpg",
                "Prentice Hall",
                2008,
                "A1-01",
                10,
                1L
        );
    }



    @Test
    void createsBookSuccessfully() {


        when(findBookPort.existsByIsbn(command.isbn()))
                .thenReturn(false);


        when(saveBookPort.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));



        Book result =
                bookManagementService.createBook(command);



        assertNotNull(result);



        verify(findBookPort)
                .existsByIsbn(command.isbn());



        verify(saveBookPort)
                .save(any(Book.class));

    }




    @Test
    void throwsExceptionWhenIsbnAlreadyExists() {


        when(findBookPort.existsByIsbn(command.isbn()))
                .thenReturn(true);



        DomainException exception =
                assertThrows(
                        DomainException.class,
                        () -> bookManagementService.createBook(command)
                );



        assertEquals(
                "ISBN đã tồn tại",
                exception.getMessage()
        );


        verify(saveBookPort, never())
                .save(any());

    }




    @Test
    void savesBookWithCorrectInformation() {


        when(findBookPort.existsByIsbn(command.isbn()))
                .thenReturn(false);



        ArgumentCaptor<Book> captor =
                ArgumentCaptor.forClass(Book.class);



        when(saveBookPort.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));



        bookManagementService.createBook(command);



        verify(saveBookPort)
                .save(captor.capture());



        Book savedBook =
                captor.getValue();



        assertEquals(
                "Clean Code",
                savedBook.getTitle()
        );


        assertEquals(
                "Robert C. Martin",
                savedBook.getAuthor()
        );


        assertEquals(
                "9780132350884",
                savedBook.getIsbn()
        );


        assertEquals(
                10,
                savedBook.getTotalQuantity()
        );


        assertEquals(
                10,
                savedBook.getAvailableQuantity()
        );

    }





    @Test
    void throwsExceptionWhenTitleIsEmpty() {


        CreateBookCommand invalidCommand =
                new CreateBookCommand(
                        "",
                        "Robert Martin",
                        "9780132350884",
                        "Software book",
                        "image.jpg",
                        "Prentice Hall",
                        2008,
                        "A1",
                        10,
                        1L
                );



        DomainException exception =
                assertThrows(
                        DomainException.class,
                        () -> bookManagementService.createBook(invalidCommand)
                );



        assertEquals(
                "Tên sách không được để trống",
                exception.getMessage()
        );


        verify(saveBookPort, never())
                .save(any());

    }





    @Test
    void throwsExceptionWhenAuthorIsEmpty() {


        CreateBookCommand invalidCommand =
                new CreateBookCommand(
                        "Clean Code",
                        "",
                        "9780132350884",
                        "Software book",
                        "image.jpg",
                        "Prentice Hall",
                        2008,
                        "A1",
                        10,
                        1L
                );



        DomainException exception =
                assertThrows(
                        DomainException.class,
                        () -> bookManagementService.createBook(invalidCommand)
                );



        assertEquals(
                "Tác giả không được để trống",
                exception.getMessage()
        );


        verify(saveBookPort, never())
                .save(any());

    }





    @Test
    void throwsExceptionWhenIsbnEmpty() {


        CreateBookCommand invalidCommand =
                new CreateBookCommand(
                        "Clean Code",
                        "Robert Martin",
                        "",
                        "Software book",
                        "image.jpg",
                        "Prentice Hall",
                        2008,
                        "A1",
                        10,
                        1L
                );



        DomainException exception =
                assertThrows(
                        DomainException.class,
                        () -> bookManagementService.createBook(invalidCommand)
                );



        assertEquals(
                "ISBN không được để trống",
                exception.getMessage()
        );


        verify(saveBookPort, never())
                .save(any());

    }





    @Test
    void throwsExceptionWhenQuantityIsZero() {


        CreateBookCommand invalidCommand =
                new CreateBookCommand(
                        "Clean Code",
                        "Robert Martin",
                        "9780132350884",
                        "Software book",
                        "image.jpg",
                        "Prentice Hall",
                        2008,
                        "A1",
                        0,
                        1L
                );



        DomainException exception =
                assertThrows(
                        DomainException.class,
                        () -> bookManagementService.createBook(invalidCommand)
                );



        assertEquals(
                "Số lượng sách phải lớn hơn 0",
                exception.getMessage()
        );


        verify(saveBookPort, never())
                .save(any());

    }





    @Test
    void throwsExceptionWhenCategoryIsNull() {


        CreateBookCommand invalidCommand =
                new CreateBookCommand(
                        "Clean Code",
                        "Robert Martin",
                        "9780132350884",
                        "Software book",
                        "image.jpg",
                        "Prentice Hall",
                        2008,
                        "A1",
                        10,
                        null
                );



        DomainException exception =
                assertThrows(
                        DomainException.class,
                        () -> bookManagementService.createBook(invalidCommand)
                );



        assertEquals(
                "Thể loại không được để trống",
                exception.getMessage()
        );


        verify(saveBookPort, never())
                .save(any());

    }





    @Test
    void throwsExceptionWhenCoverImageIsEmpty() {


        CreateBookCommand invalidCommand =
                new CreateBookCommand(
                        "Clean Code",
                        "Robert Martin",
                        "9780132350884",
                        "Software book",
                        "",
                        "Prentice Hall",
                        2008,
                        "A1",
                        10,
                        1L
                );



        DomainException exception =
                assertThrows(
                        DomainException.class,
                        () -> bookManagementService.createBook(invalidCommand)
                );



        assertEquals(
                "Ảnh bìa không được để trống",
                exception.getMessage()
        );


        verify(saveBookPort, never())
                .save(any());

    }

}