package org.example.librarymanagement.port.inbound.auth;

import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;

public interface ChangePasswordUseCase {
    ChangePasswordResult changePassword(ChangePasswordCommand command);
}
