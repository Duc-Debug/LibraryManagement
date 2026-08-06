package org.example.librarymanagement.domain.exceptions;

public class UnauthenticatedException extends DomainException {
    public UnauthenticatedException(String message) {
        super(message);
    }

    public static UnauthenticatedException defaultMessage() {
        return new UnauthenticatedException("Người dùng chưa đăng nhập hoặc phiên làm việc đã hết hạn.");
    }
}
