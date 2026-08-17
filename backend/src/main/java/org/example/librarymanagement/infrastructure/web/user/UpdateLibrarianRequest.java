package org.example.librarymanagement.infrastructure.web.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateLibrarianRequest(
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
        String phone,
        Boolean enabled // Admin có thể khóa/mở khóa tài khoản
) {

}
