package org.example.librarymanagement.port.inbound.auth;

import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
import org.example.librarymanagement.port.dtos.auth.LogoutResult;

public interface LogoutUseCase {
    LogoutResult logout(LogoutCommand command);
}
