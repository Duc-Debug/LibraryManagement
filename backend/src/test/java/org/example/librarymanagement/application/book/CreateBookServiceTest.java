package org.example.librarymanagement.application.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.entity.Category;
import org.example.librarymanagement.domain.exceptions.DuplicateResourceException;
import org.example.librarymanagement.domain.exceptions.ResourceNotFoundException;
import org.example.librarymanagement.domain.exceptions.ValidationException;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.outbound.book.BookRepositoryPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.example.librarymanagement.port.outbound.file.FileStoragePort;
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
    private SaveBookPort saveBookPort;

    @Mock
    private BookRepositoryPort bookRepositoryPort;

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private FileStoragePort fileStoragePort;

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
                null,
                "image.jpg",
                1024L,
                "Prentice Hall",
                2008,
                "A1-01",
                10,
                1L
        );
    }

    @Test
    void createsBookSuccessfully_WithoutImage() {
        when(bookRepositoryPort.existsByIsbn("9780132350884")).thenReturn(false);
        when(categoryRepositoryPort.findById(1L)).thenReturn(Optional.of(mock(Category.class)));
        when(saveBookPort.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResult result = createBookService.createBook(command);

        assertNotNull(result);
        assertEquals("Clean Code", result.title());
        assertEquals("9780132350884", result.isbn());

        verify(bookRepositoryPort).existsByIsbn("9780132350884");
        verify(categoryRepositoryPort).findById(1L);
        verify(saveBookPort).save(any(Book.class));
        verify(fileStoragePort, never()).storeBookImage(any(), any(), anyLong());
    }

    @Test
    void createsBookSuccessfully_WithImageUpload() {
        InputStream stream = new ByteArrayInputStream("image-content".getBytes());
        CreateBookCommand commandWithImage = new CreateBookCommand(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Software book",
                stream,
                "cover.jpg",
                1024L,
                "Prentice Hall",
                2008,
                "A1-01",
                10,
                1L
        );

        when(bookRepositoryPort.existsByIsbn("9780132350884")).thenReturn(false);
        when(categoryRepositoryPort.findById(1L)).thenReturn(Optional.of(mock(Category.class)));
        when(fileStoragePort.storeBookImage(stream, "cover.jpg", 1024L)).thenReturn("/uploads/books/cover.jpg");
        when(saveBookPort.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResult result = createBookService.createBook(commandWithImage);

        assertNotNull(result);
        assertEquals("/uploads/books/cover.jpg", result.coverImageUrl());

        verify(fileStoragePort).storeBookImage(stream, "cover.jpg", 1024L);
        verify(saveBookPort).save(any(Book.class));
    }

    @Test
    void rollsBackImage_WhenDBSaveFails() {
        InputStream stream = new ByteArrayInputStream("image-content".getBytes());
        CreateBookCommand commandWithImage = new CreateBookCommand(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Software book",
                stream,
                "cover.jpg",
                1024L,
                "Prentice Hall",
                2008,
                "A1-01",
                10,
                1L
        );

        when(bookRepositoryPort.existsByIsbn("9780132350884")).thenReturn(false);
        when(categoryRepositoryPort.findById(1L)).thenReturn(Optional.of(mock(Category.class)));
        when(fileStoragePort.storeBookImage(stream, "cover.jpg", 1024L)).thenReturn("/uploads/books/cover.jpg");
        
        RuntimeException dbException = new RuntimeException("Database save failed");
        when(saveBookPort.save(any(Book.class))).thenThrow(dbException);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createBookService.createBook(commandWithImage)
        );

        assertSame(dbException, thrown);
        verify(fileStoragePort).deleteFile("/uploads/books/cover.jpg");
    }

    @Test
    void preservesOriginalException_WhenBothDBSaveAndFileCleanupFail() {
        InputStream stream = new ByteArrayInputStream("image-content".getBytes());
        CreateBookCommand commandWithImage = new CreateBookCommand(
                "Clean Code",
                "Robert C. Martin",
                "9780132350884",
                "Software book",
                stream,
                "cover.jpg",
                1024L,
                "Prentice Hall",
                2008,
                "A1-01",
                10,
                1L
        );

        when(bookRepositoryPort.existsByIsbn("9780132350884")).thenReturn(false);
        when(categoryRepositoryPort.findById(1L)).thenReturn(Optional.of(mock(Category.class)));
        when(fileStoragePort.storeBookImage(stream, "cover.jpg", 1024L)).thenReturn("/uploads/books/cover.jpg");

        RuntimeException dbException = new RuntimeException("Database save failed");
        when(saveBookPort.save(any(Book.class))).thenThrow(dbException);
        
        RuntimeException cleanupException = new RuntimeException("File deletion failed");
        doThrow(cleanupException).when(fileStoragePort).deleteFile("/uploads/books/cover.jpg");

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> createBookService.createBook(commandWithImage)
        );

        assertSame(dbException, thrown);
        assertEquals(1, thrown.getSuppressed().length);
        assertSame(cleanupException, thrown.getSuppressed()[0]);
    }

    @Test
    void throwsExceptionWhenIsbnAlreadyExists() {
        when(bookRepositoryPort.existsByIsbn("9780132350884")).thenReturn(true);

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
        when(bookRepositoryPort.existsByIsbn("9780132350884")).thenReturn(false);
        when(categoryRepositoryPort.findById(command.categoryId())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> createBookService.createBook(command)
        );

        assertTrue(exception.getMessage().contains("not exist"));
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void savesBookWithCorrectInformation() {
        when(bookRepositoryPort.existsByIsbn("9780132350884")).thenReturn(false);
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
    void throwsExceptionWhenCommandIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(null)
        );

        assertEquals("Command tạo sách không được để trống", exception.getMessage());
    }

    @Test
    void throwsExceptionWhenTitleIsEmpty() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "", "Robert Martin", "9780132350884", "Software book",
                null, "image.jpg", 1024L, "Prentice Hall", 2008, "A1", 10, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Book title must not be null", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenAuthorIsEmpty() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "", "9780132350884", "Software book",
                null, "image.jpg", 1024L, "Prentice Hall", 2008, "A1", 10, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Author must not be null", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenIsbnEmpty() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "Robert Martin", "", "Software book",
                null, "image.jpg", 1024L, "Prentice Hall", 2008, "A1", 10, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("ISBN must not be null", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenQuantityIsZero() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "Robert Martin", "9780132350884", "Software book",
                null, "image.jpg", 1024L, "Prentice Hall", 2008, "A1", 0, 1L
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );

        assertEquals("Total quantity must larger than 0", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }

    @Test
    void throwsExceptionWhenCategoryIsNull() {
        CreateBookCommand invalidCommand = new CreateBookCommand(
                "Clean Code", "Robert Martin", "9780132350884", "Software book",
                null, "image.jpg", 1024L, "Prentice Hall", 2008, "A1", 10, null
        );

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> createBookService.createBook(invalidCommand)
        );


        assertEquals("Category must not be null", exception.getMessage());
        verify(saveBookPort, never()).save(any());
    }
}

