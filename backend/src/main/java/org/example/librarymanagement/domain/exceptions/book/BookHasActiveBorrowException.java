package org.example.librarymanagement.domain.exceptions.book;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class BookHasActiveBorrowException extends DomainException {
    public BookHasActiveBorrowException(Long bookId, String title) {
        super("Không thể xóa hoặc ẩn sách '" + title + "' (ID: " + bookId + ") vì sách đang nằm trong phiếu mượn chưa hoàn trả (Đang mượn hoặc Quá hạn).");
    }
}
