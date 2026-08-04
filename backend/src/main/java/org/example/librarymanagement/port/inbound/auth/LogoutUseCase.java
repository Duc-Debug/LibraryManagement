package org.example.librarymanagement.port.inbound.auth;

import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
public interface LogoutUseCase {
    void logout(LogoutCommand command);
}
