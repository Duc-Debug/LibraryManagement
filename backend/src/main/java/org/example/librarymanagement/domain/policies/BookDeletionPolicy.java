package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.book.BookHasActiveBorrowException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;

/**
 * Policy: Chặn xóa / ẩn sách nếu sách đó đang tồn tại trong phiếu mượn chưa
 * hoàn trả (Chờ trả / Quá hạn).
 */
public class BookDeletionPolicy {

    public static void validateCanDeleteOrHide(Book book, boolean hasActiveBorrowSlips) {
        if (book == null) {
            throw new InvalidBookDataException("Book does not exist in the system.");
        }

        if (hasActiveBorrowSlips) {
            throw new BookHasActiveBorrowException(book.getId(), book.getTitle());
        }
    }

    public static void validateCanDeleteOrHide(String bookTitle, long activeBorrowCount) {
        if (activeBorrowCount > 0) {
            throw new InvalidBookDataException(
                    String.format("Cannot delete or deactivate book '%s' because it has %d active borrow slip(s).",
                            bookTitle != null ? bookTitle : "Unknown", activeBorrowCount));
        }
    }
}