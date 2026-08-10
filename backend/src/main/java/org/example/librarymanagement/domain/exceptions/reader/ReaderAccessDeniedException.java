package org.example.librarymanagement.domain.exceptions.reader;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class ReaderAccessDeniedException extends DomainException {

    private ReaderAccessDeniedException(String message) {
        super(message);
    }

    public static ReaderAccessDeniedException forReader(Long readerId) {
        return new ReaderAccessDeniedException(
                "You do not have permission to manage reader with id: "
                        + readerId
        );
    }
}