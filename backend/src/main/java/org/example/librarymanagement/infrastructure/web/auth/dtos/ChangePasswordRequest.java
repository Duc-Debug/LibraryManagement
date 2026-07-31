package org.example.librarymanagement.infrastructure.web.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Old password cannot be blank") String oldPassword,
        @NotBlank(message = "New password cannot be blank") @Size(min = 6, message = "New password must be at lease 6 charaters") String newPassword,
        @NotBlank(message = "Confirm password cannot be blank") String confirmPassword) {
}
