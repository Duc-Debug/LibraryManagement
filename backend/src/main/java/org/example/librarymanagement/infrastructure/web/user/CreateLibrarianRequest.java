package org.example.librarymanagement.infrastructure.web.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không hợp lệ")
        @Pattern(
                regexp = "^[a-zA-Z0-9]+([._%+-][a-zA-Z0-9]+)*@[a-zA-Z0-9]+([.-][a-zA-Z0-9]+)*\\.[a-zA-Z]{2,}$",
                message = "Email phải chứa tên miền hợp lệ (VD: user@example.com)"
        )
        String email,

        @Pattern(
                regexp = "^$|^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",
                message = "Số điện thoại không đúng định dạng (VD: 0912345678)."
        )
        String phone
) {

}
