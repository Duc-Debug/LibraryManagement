package org.example.librarymanagement.domain.exceptions.user;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class AccountDisabledException extends DomainException {

    public AccountDisabledException() {
        super("Tài khoản của bạn đã bị tạm khóa. Vui lòng liên hệ Quản trị viên.");
    }

    public AccountDisabledException(String message) {
        super(message);
    }
}
