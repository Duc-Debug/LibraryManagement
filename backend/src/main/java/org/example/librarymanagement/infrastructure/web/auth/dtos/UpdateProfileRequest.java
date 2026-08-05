package org.example.librarymanagement.infrastructure.web.auth.dtos;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank(message = "Họ và tên không được để trống") String fullName,
        String email,
        String phone
) {
}
