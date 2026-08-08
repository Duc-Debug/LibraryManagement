package org.example.librarymanagement.infrastructure.web.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateLibrarianRequest(
        @NotBlank(message = "Username không được để trống")
        @Size(min = 4, max = 50, message = "Username phải từ 4 đến 50 ký tự")
        String username,
        @NotBlank(message = "Password không được để trống")
        @Size(min = 6, message = "Password phải có ít nhất 6 ký tự")
        String password,
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,
        @Email(message = "Email không hợp lệ")
        String email,
        String phone
        ) {

}
