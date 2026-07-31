package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class UniqueUsernamePolicy {

    /**
     * Dùng cho Tạo mới
     */
    public static void validateUsernameNotExists(boolean isUsernameExisted, String username) {
        if (isUsernameExisted) {
            throw new DomainException("Tên đăng nhập '" + username + "' đã tồn tại trong hệ thống.");
        }
    }
    /**
     * Dùng cho update
     */
    public static void validateUsernameChange(String currentUsername, String newUsername, boolean isNewUsernameExisted) {
        if (!currentUsername.equals(newUsername) && isNewUsernameExisted) {
            throw new DomainException("Tên đăng nhập mới '" + newUsername + "' đã được sử dụng bởi tài khoản khác.");
        }
    }
}