package org.example.librarymanagement.port.outbound.auth;

public interface PasswordVerifierPort {
     boolean matches(
            String rawPassword,
            String passwordHash
    );
    
}
