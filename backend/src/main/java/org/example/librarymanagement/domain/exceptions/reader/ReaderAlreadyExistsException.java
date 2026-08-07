package org.example.librarymanagement.domain.exceptions.reader;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class ReaderAlreadyExistsException extends DomainException {

    public ReaderAlreadyExistsException(String message) {
        super(message);
    }

    public static ReaderAlreadyExistsException withEmail(String email) {
        return new ReaderAlreadyExistsException("Email '" + email + "' is already registered by another reader.");
    }

    public static ReaderAlreadyExistsException withPhoneNumber(String phoneNumber) {
        return new ReaderAlreadyExistsException(
                "Phone number '" + phoneNumber + "' is already registered by another reader.");
    }

    public static ReaderAlreadyExistsException withCardNumber(String cardNumber) {
        return new ReaderAlreadyExistsException("Card number '" + cardNumber + "' already exists in the system.");
    }
}