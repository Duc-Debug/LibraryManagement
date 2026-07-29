package org.example.librarymanagement.port.inbound.auth;

public interface LogoutUseCase {
    LogoutResult logout(LogoutCommand command);
}
