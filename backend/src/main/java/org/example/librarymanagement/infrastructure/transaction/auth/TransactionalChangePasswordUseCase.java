package org.example.librarymanagement.infrastructure.transaction.auth;

import java.util.Objects;

import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for ChangePasswordUseCase
 * Bọc Spring @Transactional ngoài Pure Java ChangePasswordService
 */
public class TransactionalChangePasswordUseCase implements ChangePasswordUseCase {

    private final ChangePasswordUseCase delegate;

    public TransactionalChangePasswordUseCase(ChangePasswordUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "ChangePasswordUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        return delegate.changePassword(command);
    }
}
