package org.example.librarymanagement.application.auth;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.shared.InvalidCredentialsException;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.user.SaveUserPort;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;

public class ChangePasswordService implements ChangePasswordUseCase {
    private final LoadUserPort loadUserPort;
    private final SaveUserPort saveUserPort;
    private final PasswordVerifierPort passwordVerifierPort;

    public ChangePasswordService(
            LoadUserPort loadUserPort,
            SaveUserPort saveUserPort,
            PasswordVerifierPort passwordVerifierPort) {
        this.loadUserPort = Objects.requireNonNull(loadUserPort, "Load user port must not be null");
        this.saveUserPort = Objects.requireNonNull(saveUserPort, "Save user port must not be null");
        this.passwordVerifierPort = Objects.requireNonNull(passwordVerifierPort,
                "Password verifier port must not be null");
    }

    @Override
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        if (command == null) {
            return new ChangePasswordResult(false, "Change password command must not be null");
        }
        if (command.oldPassword() == null || command.oldPassword().isBlank()) {
            return new ChangePasswordResult(false, "Current password must not be blank");
        }
        if (command.newPassword() == null || command.newPassword().isBlank()) {
            return new ChangePasswordResult(false, "New password not null or blank");
        }
        if (!command.newPassword().equals(command.confirmPassword())) {
            return new ChangePasswordResult(false, "New password not equal confirm password");
        }
        Long userId = command.userId();
        User user = loadUserPort.findById(userId).orElseThrow(InvalidCredentialsException::new);
        boolean isOldPasswordValid = passwordVerifierPort.matches(command.oldPassword(), user.getPasswordHash());
        if (!isOldPasswordValid) {
            return new ChangePasswordResult(false, "Current password not exactly");
        }
        boolean isSameAsCurrentPassword = passwordVerifierPort.matches(command.newPassword(), user.getPasswordHash());
        if (isSameAsCurrentPassword) {
            return new ChangePasswordResult(false, "New password must be different from current password");
        }
        String newPasswordHash = passwordVerifierPort.encode(command.newPassword());
        user.changePassword(newPasswordHash);

        saveUserPort.save(user);
        return new ChangePasswordResult(true, "Password changed successfully");
    }
}