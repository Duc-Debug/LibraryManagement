package org.example.librarymanagement.domain.policies;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.shared.AccessDeniedException;

public class AuthorizationAccessPolicy {
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_LIBRARIAN = "LIBRARIAN";

    /**
     * Kiểm tra xem user có quyền ADMIN để thực hiện các chức năng quản trị hay không.
     */
    public static void validateAdminAccess(User user) {
        if (user == null || !user.hasRole(ROLE_ADMIN)) {
            throw new AccessDeniedException("Access denied: This feature requires Administrator (ADMIN) role.");
        }
    }

    /**
     * Kiểm tra vai trò tối thiểu để thao tác nghiệp vụ thư viện (Admin hoặc Thủ thư).
     */
    public static void validateStaffAccess(User user) {
        if (user == null || (!user.hasRole(ROLE_ADMIN) && !user.hasRole(ROLE_LIBRARIAN))) {
            throw new AccessDeniedException("Access denied: You do not have staff permissions to perform library operations.");
        }
    }
}