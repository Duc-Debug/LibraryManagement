package org.example.librarymanagement.domain.exceptions.borrow;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class BorrowLimitExceededException extends DomainException {

    private final int currentBorrowingCount;
    private final int requestedBooksCount;
    private final int maxLimit;

    public BorrowLimitExceededException(int currentBorrowingCount, int requestedBooksCount, int maxLimit) {
        super(String.format(
                "Borrow limit exceeded: Reader is currently borrowing %d book(s), requesting %d book(s), but max allowed limit is %d book(s).",
                currentBorrowingCount, requestedBooksCount, maxLimit
        ));
        this.currentBorrowingCount = currentBorrowingCount;
        this.requestedBooksCount = requestedBooksCount;
        this.maxLimit = maxLimit;
    }

    public BorrowLimitExceededException(String message) {
        super(message);
        this.currentBorrowingCount = 0;
        this.requestedBooksCount = 0;
        this.maxLimit = 0;
    }

    public int getCurrentBorrowingCount() {
        return currentBorrowingCount;
    }

    public int getRequestedBooksCount() {
        return requestedBooksCount;
    }

    public int getMaxLimit() {
        return maxLimit;
    }
}