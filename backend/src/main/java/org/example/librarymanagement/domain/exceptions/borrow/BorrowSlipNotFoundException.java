package org.example.librarymanagement.domain.exceptions.borrow;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class BorrowSlipNotFoundException extends DomainException {
    public BorrowSlipNotFoundException(Long id) {
        super("Borrow slip not found with ID: " + id);
    }
}