package org.example.librarymanagement.port.dtos.auth;

public record ChangePasswordCommand(
        Long userId,
        String oldPassword,
        String newPassword,
        String confirmPassword) {
}