package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;

public class UserAccountPolicy {
    public static void validateAccountActive(User user) {
        if (user == null) {
            throw new DomainException("Tài khoản không tồn tại trong hệ thống.");
        }

        if (!user.isEnabled()) {
            throw new DomainException("Tài khoản đã bị khóa hoặc ngừng hoạt động. Vui lòng liên hệ Admin.");
        }
    }
}
