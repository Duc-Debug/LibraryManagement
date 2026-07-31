package org.example.librarymanagement.port.inbound.auth;

import org.example.librarymanagement.port.dtos.auth.LoginCommand;
import org.example.librarymanagement.port.dtos.auth.LoginResult;

public interface LoginUseCase {

    LoginResult login(LoginCommand command);
}
