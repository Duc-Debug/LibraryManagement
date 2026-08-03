package org.example.librarymanagement.port.inbound.manage;

public record CreateUserCommand(
        String username,
        String rawPassword,
        String fullName,
        String email,
        String phone,
        String roleName // Ví dụ: "LIBRARIAN"
        ) {

}
