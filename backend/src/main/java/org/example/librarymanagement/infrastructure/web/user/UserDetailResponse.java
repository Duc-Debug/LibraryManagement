package org.example.librarymanagement.infrastructure.web.user;

import java.time.LocalDateTime;
import java.util.Set;

public record UserDetailResponse(
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
