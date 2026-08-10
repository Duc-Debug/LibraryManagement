package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.Reader;
import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class BorrowSlipCreationPolicy {

    public static void validateCanBorrow(Reader reader) {
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
}