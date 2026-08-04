package org.example.librarymanagement.infrastructure.web.auth.dtos;

import java.util.Set;

public record LoginResponse(
        Long userId,
        String username,
        String fullName,
        Set<String> roles,
        String accessToken,
        String tokenType
) {
}