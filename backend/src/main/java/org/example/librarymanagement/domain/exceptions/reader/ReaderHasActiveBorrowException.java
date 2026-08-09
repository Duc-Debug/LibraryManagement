package org.example.librarymanagement.domain.exceptions.reader;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class ReaderHasActiveBorrowException extends DomainException {

    private ReaderHasActiveBorrowException(String message) {
        super(message);
    }

    public static ReaderHasActiveBorrowException withReaderId(
            Long readerId) {
        return new ReaderHasActiveBorrowException(
                "Cannot delete reader with id "
                        + readerId
                        + " because the reader has an active borrow.");
    }
}