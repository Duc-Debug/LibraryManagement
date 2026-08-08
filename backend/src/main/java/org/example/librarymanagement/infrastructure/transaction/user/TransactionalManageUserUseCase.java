package org.example.librarymanagement.infrastructure.transaction.user;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.port.dtos.user.CreateUserCommand;
import org.example.librarymanagement.port.dtos.user.UpdateUserCommand;
import org.example.librarymanagement.port.dtos.user.UserResult;
import org.example.librarymanagement.port.inbound.user.ManageUserUseCase;
import org.springframework.transaction.annotation.Transactional;

/**
 * Transactional Decorator Proxy for ManageUserUseCase
 * Bọc Spring @Transactional ngoài Pure Java UserManagementService
 */
public class TransactionalManageUserUseCase implements ManageUserUseCase {

    private final ManageUserUseCase delegate;

    public TransactionalManageUserUseCase(ManageUserUseCase delegate) {
        this.delegate = Objects.requireNonNull(delegate, "ManageUserUseCase delegate must not be null");
    }

    @Override
    @Transactional
    public UserResult createUser(CreateUserCommand command) {
        return delegate.createUser(command);
    }

    @Override
    @Transactional
    public UserResult updateUser(UpdateUserCommand command) {
        return delegate.updateUser(command);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        delegate.deactivateUser(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResult getUserById(Long userId) {
        return delegate.getUserById(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResult> getAllUsersByRole(String roleName) {
        return delegate.getAllUsersByRole(roleName);
    }
}
