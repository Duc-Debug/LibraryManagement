package org.example.librarymanagement.port.inbound.auth;

import org.example.librarymanagement.port.dtos.auth.UpdateProfileCommand;
import org.example.librarymanagement.port.inbound.manage.UserResult;

public interface ProfileUseCase {
    UserResult getProfile(Long userId);
    UserResult updateProfile(Long userId, UpdateProfileCommand command);
}
