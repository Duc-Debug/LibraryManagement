package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.infrastructure.persistence.user.UserJpaRepository;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.SaveUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePasswordService implements ChangePasswordUseCase {

    private final LoadUserPort userRepositoryPort;
    private final SaveUserPort saveUserPort;
    private final PasswordVerifierPort passwordVerifierPort;

    @Override
    @Transactional
    public ChangePasswordResult changePassword(ChangePasswordCommand command) {
        if (command.newPassword() == null || command.newPassword().isBlank()) {
            return new ChangePasswordResult(false, "New password not null or blank");
        }
        if (!command.newPassword().equals(command.confirmPassword())) {
            return new ChangePasswordResult(false, "New password not equal confirm password");
        }
        Long userId = command.userId();
        User user = userRepositoryPort.findByUserId(userId).orElseThrow(InvalidCredentialsException::new);
        boolean isOldPasswordVaild = passwordVerifierPort.matches(command.oldPassword(), user.getPasswordHash());
        if (!isOldPasswordVaild) {
            return new ChangePasswordResult(false, "Current password not exactly");
        }
        String newPasswordHash = passwordVerifierPort.encode(command.newPassword());
        user.changePassword(newPasswordHash);

        saveUserPort.save(user);
        return new ChangePasswordResult(true,"Password changed successfully");
    }

}
