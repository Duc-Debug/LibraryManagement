package org.example.librarymanagement.port.outbound.borrow;

public interface CheckActiveReaderBorrowPort {

    boolean hasActiveBorrowByReaderId(Long readerId);
}