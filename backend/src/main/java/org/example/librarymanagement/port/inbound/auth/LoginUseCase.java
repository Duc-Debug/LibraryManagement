package org.example.librarymanagement.port.inbound.auth;

public interface LoginUseCase {

    LoginResult login(LoginCommand command);
}
