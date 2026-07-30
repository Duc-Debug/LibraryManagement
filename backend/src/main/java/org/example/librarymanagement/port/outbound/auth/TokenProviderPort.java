package org.example.librarymanagement.port.outbound.auth;

import org.example.librarymanagement.domain.auth.User;

public interface TokenProviderPort {

    String generateAccessToken(User user);
}
