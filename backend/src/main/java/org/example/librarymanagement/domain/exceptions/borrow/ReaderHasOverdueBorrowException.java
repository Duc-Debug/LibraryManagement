package org.example.librarymanagement.domain.exceptions.borrow;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class ReaderHasOverdueBorrowException extends DomainException {

    private final String readerCardNumber;

    public ReaderHasOverdueBorrowException(String readerCardNumber) {
        super(String.format(
                "Reader [Card: %s] has overdue borrowed books that have not been returned. Cannot borrow new books.",
                readerCardNumber != null ? readerCardNumber : "N/A"
        ));
        this.readerCardNumber = readerCardNumber;
    }

    public ReaderHasOverdueBorrowException(Long readerId, String readerCardNumber) {
        super(String.format(
                "Reader (ID: %s, Card: %s) has overdue borrowed books that have not been returned. Cannot borrow new books.",
                readerId != null ? readerId : "N/A",
                readerCardNumber != null ? readerCardNumber : "N/A"
        ));
        this.readerCardNumber = readerCardNumber;
    }

    public String getReaderCardNumber() {
        return readerCardNumber;
    }
}
