package org.example.librarymanagement.application.book;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.Category;
import org.example.librarymanagement.domain.exceptions.ValidationException;
import org.example.librarymanagement.domain.exceptions.DuplicateResourceException;
import org.example.librarymanagement.domain.exceptions.ResourceNotFoundException;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.outbound.book.FindBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateBookServiceTest {

    @Mock
    private FindBookPort findBookPort;

    @Mock
    private SaveBookPort saveBookPort;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @InjectMocks
    private CreateBookService createBookService;

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
        when(findBookPort.existsByIsbn(command.isbn())).thenReturn(false);
        when(categoryRepositoryPort.findById(command.categoryId())).thenReturn(Optional.of(mock(Category.class)));
        when(saveBookPort.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResult result = createBookService.createBook(command);

        assertNotNull(result);
        assertEquals("Clean Code", result.title());

        verify(findBookPort).existsByIsbn(command.isbn());
        verify(categoryRepositoryPort).findById(command.categoryId());
        verify(saveBookPort).save(any(Book.class));
    }

    @Test
    void throwsExceptionWhenIsbnAlreadyExists() {
        when(findBookPort.existsByIsbn(command.isbn())).thenReturn(true);

        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> createBookService.createBook(command)
        );

        assertTrue(exception.getMessage().contains("đã tồn tại"));

        verify(categoryRepositoryPort, never()).findById(any());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenCategoryDoesNotExist() {
        when(findBookPort.existsByIsbn(command.isbn())).thenReturn(false);
        when(categoryRepositoryPort.findById(command.categoryId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> createBookService.createBook(command)
        );

        assertTrue(exception.getMessage().contains("không tồn tại"));
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void savesBookWithCorrectInformation() {
        when(findBookPort.existsByIsbn(command.isbn())).thenReturn(false);
        when(categoryRepositoryPort.findById(command.categoryId())).thenReturn(Optional.of(mock(Category.class)));

        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
        when(saveBookPort.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        createBookService.createBook(command);

        verify(saveBookPort).save(captor.capture());
        Book savedBook = captor.getValue();

        assertEquals("Clean Code", savedBook.getTitle());
        assertEquals("Robert C. Martin", savedBook.getAuthor());
        assertEquals("9780132350884", savedBook.getIsbn()); 
        assertEquals(10, savedBook.getTotalQuantity());
        assertEquals(10, savedBook.getAvailableQuantity());
        assertTrue(savedBook.isActive());
    }

    @Test
    void throwsExceptionWhenTitleIsEmpty() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "", "Robert Martin", "9780132350884", "Software book",
                "image.jpg", "Prentice Hall", 2008, "A1", 10, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Tên sách không được để trống", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenAuthorIsEmpty() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "", "9780132350884", "Software book",
                "image.jpg", "Prentice Hall", 2008, "A1", 10, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Tác giả không được để trống", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenIsbnEmpty() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "Robert Martin", "", "Software book",
                "image.jpg", "Prentice Hall", 2008, "A1", 10, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("ISBN không được để trống", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenQuantityIsZero() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "Robert Martin", "9780132350884", "Software book",
                "image.jpg", "Prentice Hall", 2008, "A1", 0, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Số lượng sách phải lớn hơn 0", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenCategoryIsNull() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "Robert Martin", "9780132350884", "Software book",
                "image.jpg", "Prentice Hall", 2008, "A1", 10, null
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Thể loại không được để trống", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenCoverImageIsEmpty() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "Robert Martin", "9780132350884", "Software book",
                "", "Prentice Hall", 2008, "A1", 10, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Ảnh bìa không được để trống", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }
}