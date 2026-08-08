package org.example.librarymanagement.port.dtos.user;

public record CreateUserCommand(
        String username,
        String rawPassword,
        String fullName,
        String email,
        String phone,
        String roleName // Ví dụ: "LIBRARIAN"
        ) {

}
