package org.example.librarymanagement.port.outbound.auth;

public interface TokenBlacklistPort {
    void blacklistToken(String token);
}
