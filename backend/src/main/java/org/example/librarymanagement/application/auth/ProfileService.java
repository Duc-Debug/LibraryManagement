package org.example.librarymanagement.application.auth;

import java.util.Set;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.dtos.auth.UpdateProfileCommand;
import org.example.librarymanagement.port.inbound.auth.ProfileUseCase;
import org.example.librarymanagement.port.inbound.manage.UserResult;
import org.example.librarymanagement.port.outbound.manage.FindUserPort;
import org.example.librarymanagement.port.outbound.manage.SaveUserPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProfileService implements ProfileUseCase {

    private final FindUserPort findUserPort;
    private final SaveUserPort saveUserPort;

    @Override
    public UserResult getProfile(Long userId) {
        User user = findUserPort.findById(userId)
                .orElseThrow(() -> new DomainException("Không tìm thấy thông tin người dùng."));
        return mapToResult(user);
    }

    @Override
    public UserResult updateProfile(Long userId, UpdateProfileCommand command) {
        User user = findUserPort.findById(userId)
                .orElseThrow(() -> new DomainException("Không tìm thấy thông tin người dùng."));

        user.updateProfile(command.fullName(), command.email(), command.phone());
        User updatedUser = saveUserPort.save(user);
        return mapToResult(updatedUser);
    }

    private UserResult mapToResult(User user) {
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new UserResult(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isEnabled(),
                roleNames,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
