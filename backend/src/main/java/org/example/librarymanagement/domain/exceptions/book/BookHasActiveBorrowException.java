package org.example.librarymanagement.domain.exceptions.book;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class BookHasActiveBorrowException extends DomainException {

    public BookHasActiveBorrowException(Long bookId, String title) {
        super("Cannot delete or deactivate book '" + title + "' (ID: " + bookId 
                + ") because it currently has active borrow slips (Borrowing or Overdue).");
    }
}