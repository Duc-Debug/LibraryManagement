package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class UniqueUsernamePolicy {

    /**
     * Kiểm tra khi tạo tài khoản mới.
     * @param isUsernameExisted Kết quả truy vấn từ UserRepository (true nếu đã tồn tại).
     * @param username Tên đăng nhập cần kiểm tra.
     */
    public static void validateUsernameForCreate(boolean isUsernameExisted, String username) {
        if (isUsernameExisted) {
            throw new DomainException("Tên đăng nhập '" + username + "' đã tồn tại trong hệ thống.");
        }
    }

    /**
     * Kiểm tra khi cập nhật thông tin tài khoản.
     * @param currentUsername Tên đăng nhập hiện tại.
     * @param newUsername Tên đăng nhập mới muốn đổi.
     * @param isNewUsernameExisted Kết quả truy vấn tên mới từ UserRepository.
     */
    public static void validateUsernameForUpdate(String currentUsername, String newUsername, boolean isNewUsernameExisted) {
        if (!currentUsername.equalsIgnoreCase(newUsername) && isNewUsernameExisted) {
            throw new DomainException("Tên đăng nhập mới '" + newUsername + "' đã được sử dụng bởi tài khoản khác.");
        }
    }
}