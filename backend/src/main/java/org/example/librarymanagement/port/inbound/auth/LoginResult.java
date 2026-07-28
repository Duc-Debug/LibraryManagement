package org.example.librarymanagement.port.inbound.auth;

import java.util.Set;

public record LoginResult(
        Long userId,
        String username,
        String fullName,
        Set<String> roles,
        String accessToken
) {
}
