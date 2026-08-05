package org.example.librarymanagement.port.dtos.auth;

public record UpdateProfileCommand(
        String fullName,
        String email,
        String phone
) {
}
