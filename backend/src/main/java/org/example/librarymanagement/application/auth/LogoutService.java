package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.example.librarymanagement.port.outbound.auth.TokenBlacklistPort;

public class LogoutService implements LogoutUseCase {

    private final TokenBlacklistPort tokenBlacklistPort;

    public LogoutService(TokenBlacklistPort tokenBlacklistPort) {
        this.tokenBlacklistPort = tokenBlacklistPort;
    }

    @Override
    public void logout(LogoutCommand command) {
        if (command == null) {
            throw new ValidationException(
            "Logout command cannot be null."
    );
        }

        String token = command.token();

        if (token == null || token.isBlank()) {
            throw new ValidationException(
            "Token cannot be null or empty.");
        }

        tokenBlacklistPort.blacklistToken(token);
    }
}
