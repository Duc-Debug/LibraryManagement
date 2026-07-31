package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
import org.example.librarymanagement.port.dtos.auth.LogoutResult;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.example.librarymanagement.port.outbound.auth.TokenBlacklistPort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import chuẩn của Lombok Slf4j

@Slf4j // Lombok sẽ tự sinh biến 'log' chuẩn ở background
@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutUseCase {

    // Không khai báo biến 'log' ở đây nữa!

    private final TokenBlacklistPort tokenBlacklistPort;

    @Override
    public LogoutResult logout(LogoutCommand command) {
        String token = command.token();

        if (token == null || token.trim().isEmpty()) {
            log.warn("Logout attempted with null or empty token.");
            return new LogoutResult(false, "Token cannot be null or empty.");
        }

        try {
            tokenBlacklistPort.blacklistToken(token);
            log.info("Token successfully blacklisted.");
            return new LogoutResult(true, "Logout successful.");
        } catch (Exception e) {
            log.error("Error occurred while blacklisting token: {}", e.getMessage());
            return new LogoutResult(false, "Logout failed due to an internal error.");
        }
    }
}