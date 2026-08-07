package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.domain.exceptions.book.BookHasActiveBorrowException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;

/**
 * - Chặn xóa / ẩn sách nếu sách đó đang tồn tại trong phiếu mượn chưa hoàn trả (Chờ trả / Quá hạn).
 */
public class BookDeletionPolicy {

    public static void validateCanDeleteOrHide(Book book, boolean hasActiveBorrowSlips) {
        if (book == null) {
            throw new InvalidBookDataException("Sách không tồn tại trong hệ thống.");
        }

        if (hasActiveBorrowSlips) {
            throw new BookHasActiveBorrowException(book.getId(), book.getTitle());
        }
    }

    public static void validateCanDeleteOrHide(String bookTitle, long activeBorrowCount) {
        if (activeBorrowCount > 0) {
            throw new InvalidBookDataException(
                String.format("Không thể xóa hoặc ẩn sách '%s' vì đang có %d phiếu mượn chưa hoàn trả liên quan.",
                    bookTitle != null ? bookTitle : "không xác định", activeBorrowCount)
            );
        }
    }
}
