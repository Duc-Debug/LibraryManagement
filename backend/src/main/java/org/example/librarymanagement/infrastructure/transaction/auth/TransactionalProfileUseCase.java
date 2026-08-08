package org.example.librarymanagement.infrastructure.transaction.auth;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.auth.UpdateProfileCommand;
import org.example.librarymanagement.port.dtos.user.UserResult;
import org.example.librarymanagement.port.inbound.auth.ProfileUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for ProfileUseCase
 * Bọc Spring @Transactional ngoài Pure Java ProfileService
 */
public class TransactionalProfileUseCase implements ProfileUseCase {

    private final ProfileUseCase delegate;

    public TransactionalProfileUseCase(ProfileUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "ProfileUseCase delegate must not be null");
    }

    @Override
    @Transactional(readOnly = true)
    public UserResult getProfile(Long userId) {
        return delegate.getProfile(userId);
    }

    @Override
    @Transactional
    public UserResult updateProfile(Long userId, UpdateProfileCommand command) {
        return delegate.updateProfile(userId, command);
    }
}
