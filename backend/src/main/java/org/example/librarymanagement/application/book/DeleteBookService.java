package org.example.librarymanagement.application.book;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.DomainException;
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
            CheckActiveBorrowPort checkActiveBorrowPort
    ) {
        this.loadBookPort = Objects.requireNonNull(loadBookPort, "LoadBookPort must not be null");
        this.saveBookPort = Objects.requireNonNull(saveBookPort, "SaveBookPort must not be null");
        this.checkActiveBorrowPort = Objects.requireNonNull(checkActiveBorrowPort, "CheckActiveBorrowPort must not be null");
    }

    @Override
    public void deleteBook(Long bookId) {
        validateBookId(bookId);

        Book book = loadBookPort.findById(bookId)
                .orElseThrow(() -> new DomainException("Không tìm thấy sách với ID: " + bookId));

        boolean hasActiveBorrowSlips = checkActiveBorrowPort.hasActiveBorrowSlips(bookId);

        BookDeletionPolicy.validateCanDeleteOrHide(book, hasActiveBorrowSlips);

        saveBookPort.deleteById(bookId);
    }

    @Override
    public void hideBook(Long bookId) {
        validateBookId(bookId);

        Book book = loadBookPort.findById(bookId)
                .orElseThrow(() -> new DomainException("Không tìm thấy sách với ID: " + bookId));

        boolean hasActiveBorrowSlips = checkActiveBorrowPort.hasActiveBorrowSlips(bookId);

        BookDeletionPolicy.validateCanDeleteOrHide(book, hasActiveBorrowSlips);

        book.deactivate();

        saveBookPort.save(book);
    }

    @Override
    public void unhideBook(Long bookId) {
        validateBookId(bookId);

        Book book = loadBookPort.findById(bookId)
                .orElseThrow(() -> new DomainException("Không tìm thấy sách với ID: " + bookId));

        book.activate();

        saveBookPort.save(book);
    }

    private void validateBookId(Long bookId) {
        if (bookId == null || bookId <= 0) {
            throw new DomainException("Book ID must be greater than 0");
        }
    }
}
