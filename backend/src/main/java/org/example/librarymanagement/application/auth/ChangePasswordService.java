package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.SaveUserPort;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;

public class ChangePasswordService implements ChangePasswordUseCase {
    private final LoadUserPort userRepositoryPort;
    private final SaveUserPort saveUserPort;
    private final PasswordVerifierPort passwordVerifierPort;

    public ChangePasswordService(
            LoadUserPort userRepositoryPort,
            SaveUserPort saveUserPort,
            PasswordVerifierPort passwordVerifierPort
    ) {
        this.userRepositoryPort = userRepositoryPort;
        this.saveUserPort = saveUserPort;
        this.passwordVerifierPort = passwordVerifierPort;
    }

    @Override
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        if (command.newPassword() == null || command.newPassword().isBlank()) {
            return new ChangePasswordResult(false, "New password not null or blank");
        }
        if (!command.newPassword().equals(command.confirmPassword())) {
            return new ChangePasswordResult(false, "New password not equal confirm password");
        }
        Long userId = command.userId();
        User user = userRepositoryPort.findById(userId).orElseThrow(InvalidCredentialsException::new);
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