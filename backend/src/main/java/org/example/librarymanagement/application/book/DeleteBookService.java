package org.example.librarymanagement.application.book;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.book.BookNotFoundException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;
import org.example.librarymanagement.domain.policies.BookDeletionPolicy;
import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.example.librarymanagement.port.outbound.book.LoadBookPort;
import org.example.librarymanagement.port.outbound.book.SaveBookPort;
import org.example.librarymanagement.port.outbound.borrow.CheckActiveBorrowPort;

public class DeleteBookService implements DeleteBookUseCase {

    private final LoadBookPort loadBookPort;
    private final SaveBookPort saveBookPort;
    private final CheckActiveBorrowPort checkActiveBorrowPort;

    public DeleteBookService(
            LoadBookPort loadBookPort,
            SaveBookPort saveBookPort,
            CheckActiveBorrowPort checkActiveBorrowPort) {
        this.loadBookPort = Objects.requireNonNull(loadBookPort, "LoadBookPort must not be null");
        this.saveBookPort = Objects.requireNonNull(saveBookPort, "SaveBookPort must not be null");
        this.checkActiveBorrowPort = Objects.requireNonNull(checkActiveBorrowPort,
                "CheckActiveBorrowPort must not be null");
    }

    @Override
    public void deleteBook(Long bookId) {
        Book book = loadBookById(bookId);

        validateBookForDeletion(book);
        saveBookPort.deleteById(bookId);
    }

    @Override
    public void hideBook(Long bookId) {
        Book book = loadBookById(bookId);
        validateBookForDeletion(book);

        book.deactivate();
        saveBookPort.save(book);
    }

    @Override
    public void unhideBook(Long bookId) {
        Book book = loadBookById(bookId);
        book.activate();
        saveBookPort.save(book);
    }

    private Book loadBookById(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new InvalidBookDataException("Book ID must be greater than 0");
        }
        return loadBookPort.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));
    }

    private void validateBookForDeletion(Book book) {
        boolean hasActiveBorrowSlips = checkActiveBorrowPort.hasActiveBorrowSlips(book.getId());
        BookDeletionPolicy.validateCanDeleteOrHide(book, hasActiveBorrowSlips);
    }
}