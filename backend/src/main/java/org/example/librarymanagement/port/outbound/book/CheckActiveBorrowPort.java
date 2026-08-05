package org.example.librarymanagement.port.outbound.book;

import java.util.UUID;

public interface CheckActiveBorrowPort {
    boolean hasActiveBorrowSlips(UUID bookId);
}
