package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.borrow.BorrowLimitExceededException;
import org.example.librarymanagement.domain.exceptions.borrow.ReaderHasOverdueBorrowException;

public class BorrowSlipCreationPolicy {

    public static final int DEFAULT_MAX_CONCURRENT_BORROW_LIMIT = 5;

    /**
     * Kiểm tra trạng thái tài khoản và thẻ độc giả
     */
    public static void validateCanBorrow(Reader reader) {
        if (reader == null) {
            throw new DomainException("Reader must not be null");
        }
        if (!reader.isActive()) {
            throw new DomainException("Reader account is inactive, cannot create borrow slip.");
        }
        if (reader.getCardStatus() == CardStatus.LOCKED) {
            throw new DomainException("Reader card is locked, cannot create borrow slip.");
        }
        if (reader.getCardStatus() == CardStatus.EXPIRED) {
            throw new DomainException("Reader card has expired, cannot create borrow slip.");
        }
    }

    /**
     * Chặn mượn sách mới nếu độc giả đang có sách quá hạn chưa hoàn trả
     */
    public static void validateNoOverdueBooks(boolean hasOverdueBooks, String readerCardNumber) {
        if (hasOverdueBooks) {
            throw new ReaderHasOverdueBorrowException(readerCardNumber);
        }
    }

    /**
     * Chặn mượn sách khi số sách mượn đồng thời vượt quá hạn mức tối đa cho phép
     */
    public static void validateBorrowLimit(int currentBorrowingCount, int requestedBooksCount, int maxLimit) {
        if (maxLimit <= 0) {
            throw new DomainException("Maximum borrow limit must be greater than 0");
        }
        if (currentBorrowingCount < 0) {
            throw new DomainException("Current borrowing count cannot be negative");
        }
        if (requestedBooksCount <= 0) {
            throw new DomainException("Requested books count must be greater than 0");
        }
        if (currentBorrowingCount + requestedBooksCount > maxLimit) {
            throw new BorrowLimitExceededException(currentBorrowingCount, requestedBooksCount, maxLimit);
        }
    }
}