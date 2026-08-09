package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class AccountLockPolicy {

    /**
     * Kiểm tra tài khoản có đang hoạt động không trước khi cho phép Đăng nhập /
     * Thực hiện Use Case.
     */
    public static void validateAccountActive(User user) {
        if (user == null) {
            throw new DomainException("User account does not exist.");
        }
        user.ensureCanLogin();
    }
}