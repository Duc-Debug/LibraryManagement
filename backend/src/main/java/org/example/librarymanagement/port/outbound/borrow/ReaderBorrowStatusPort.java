package org.example.librarymanagement.port.outbound.borrow;

public interface ReaderBorrowStatusPort {


    boolean hasActiveBorrowByReaderId(Long readerId);
}