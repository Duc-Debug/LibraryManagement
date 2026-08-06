package org.example.librarymanagement.port.outbound.borrow;

public interface CheckActiveBorrowPort {

    boolean hasActiveBorrowSlips(Long bookId);

}
