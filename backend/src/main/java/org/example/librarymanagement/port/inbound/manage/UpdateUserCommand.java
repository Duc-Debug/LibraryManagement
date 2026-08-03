package org.example.librarymanagement.port.inbound.manage;

public record UpdateUserCommand(
        Long userId,
        String fullName,
        String email,
        String phone,
        Boolean enabled // Cho phép Admin khóa/mở khóa tài khoản
        ) {

}
