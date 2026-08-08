package org.example.librarymanagement.port.dtos.user;

import java.time.LocalDateTime;
import java.util.Set;

public record UserResult(
        Long id,
        String username,
        String fullName,
        String email,
        String phone,
        boolean enabled,
        Set<String> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

}
