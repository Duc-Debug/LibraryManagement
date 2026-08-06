package org.example.librarymanagement.infrastructure.web.reader;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateReaderRequest(
        @NotBlank(message = "Họ tên không được để trống")
        String name,

        @NotBlank(message = "Email không được để trống")
        @Email(message = "Email không đúng định dạng")
        String email,

        @NotBlank(message = "Số điện thoại không được để trống")
        @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng (VD: 0912345678)")
        String phoneNumber,

        @NotBlank(message = "Địa chỉ không được để trống")
        String address
) {
}
