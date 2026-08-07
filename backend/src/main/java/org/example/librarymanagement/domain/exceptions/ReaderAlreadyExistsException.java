package org.example.librarymanagement.domain.exceptions;

public class ReaderAlreadyExistsException extends DomainException {

    public ReaderAlreadyExistsException(String message) {
        super(message);
    }

    public static ReaderAlreadyExistsException withEmail(String email) {
        return new ReaderAlreadyExistsException("Email '" + email + "' đã được đăng ký bởi bạn đọc khác.");
    }

    public static ReaderAlreadyExistsException withPhoneNumber(String phoneNumber) {
        return new ReaderAlreadyExistsException("Số điện thoại '" + phoneNumber + "' đã được đăng ký bởi bạn đọc khác.");
    }

    public static ReaderAlreadyExistsException withCardNumber(String cardNumber) {
        return new ReaderAlreadyExistsException("Mã thẻ bạn đọc '" + cardNumber + "' đã tồn tại trong hệ thống.");
    }
}
