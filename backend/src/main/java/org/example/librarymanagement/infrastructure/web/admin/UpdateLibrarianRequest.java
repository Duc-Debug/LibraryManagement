package org.example.librarymanagement.infrastructure.web.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateLibrarianRequest(
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,
        @Email(message = "Email không hợp lệ")
        String email,
        String phone,
        Boolean enabled // Admin có thể khóa/mở khóa tài khoản
        ) {

}
