package org.example.librarymanagement.domain.exceptions.user;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class AccountDisabledException extends DomainException {

    public AccountDisabledException() {
        super("ACCOUNT_DISABLED");
    }

    public AccountDisabledException(String message) {
        super(message);
    }
}
