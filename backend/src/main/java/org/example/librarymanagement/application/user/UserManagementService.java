package org.example.librarymanagement.application.user;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.domain.policies.UniqueUsernamePolicy;
import org.example.librarymanagement.port.dtos.user.CreateUserCommand;
import org.example.librarymanagement.port.dtos.user.UpdateUserCommand;
import org.example.librarymanagement.port.dtos.user.UserResult;
import org.example.librarymanagement.port.inbound.user.ManageUserUseCase;
import org.example.librarymanagement.port.outbound.user.EncodePasswordPort;
import org.example.librarymanagement.port.outbound.user.FindUserPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.user.LoadRolePort;
import org.example.librarymanagement.port.outbound.user.SaveUserPort;

/**
 * Application Service: UserManagementService
 * Pure Java 100% - Hexagonal Architecture Implementation
 */
public class UserManagementService implements ManageUserUseCase {

    private final FindUserPort findUserPort;
    private final LoadRolePort loadRolePort;
    private final SaveUserPort saveUserPort;
    private final EncodePasswordPort encodePasswordPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    public UserManagementService(
            FindUserPort findUserPort,
            LoadRolePort loadRolePort,
            SaveUserPort saveUserPort,
            EncodePasswordPort encodePasswordPort,
            GetAuthenticatedUserPort getAuthenticatedUserPort
    ) {
        this.findUserPort = Objects.requireNonNull(findUserPort, "Find user port must not be null");
        this.loadRolePort = Objects.requireNonNull(loadRolePort, "Load role port must not be null");
        this.saveUserPort = Objects.requireNonNull(saveUserPort, "Save user port must not be null");
        this.encodePasswordPort = Objects.requireNonNull(encodePasswordPort, "Encode password port must not be null");
        this.getAuthenticatedUserPort = Objects.requireNonNull(getAuthenticatedUserPort, "Get authenticated user port must not be null");
    }

    @Override
    public UserResult createUser(CreateUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Create user command must not be null");
        }

        // 1. Phân quyền Admin
        verifyAdminAccess();

        // 2. Kiểm tra trùng lặp Username qua Domain Policy
        boolean isUsernameExisted = findUserPort.existsByUsername(command.username());
        UniqueUsernamePolicy.validateUsernameForCreate(isUsernameExisted, command.username());

        // 3. Tải Role từ Outbound Port
        Role role = loadRolePort.findByName(command.roleName())
                .orElseThrow(() -> new DomainException("Role not found: " + command.roleName()));

        // 4. Mã hóa mật khẩu qua Password Encoder Port
        String encodedPassword = encodePasswordPort.encode(command.rawPassword());

        // 5. Khởi tạo Domain Entity bằng Static Factory Method
        User newUser = User.create(
                command.username(),
                encodedPassword,
                command.fullName(),
                command.email(),
                command.phone()
        );
        newUser.addRole(role);

        // 6. Lưu xuống DB qua Outbound Port & Map sang Result DTO
        User savedUser = saveUserPort.save(newUser);
        return mapToResult(savedUser);
    }

    @Override
    public UserResult updateUser(UpdateUserCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("Update user command must not be null");
        }

        verifyAdminAccess();

        User targetUser = findUserPort.findById(command.userId())
                .orElseThrow(() -> new DomainException("User not found with ID: " + command.userId()));

        targetUser.updateProfile(command.fullName(), command.email(), command.phone());

        if (command.enabled() != null) {
            if (command.enabled()) {
                targetUser.activate();
            } else {
                targetUser.deactivate();
            }
        }

        User updatedUser = saveUserPort.save(targetUser);
        return mapToResult(updatedUser);
    }

    @Override
    public void deactivateUser(Long userId) {
        verifyAdminAccess();

        User targetUser = findUserPort.findById(userId)
                .orElseThrow(() -> new DomainException("User not found with ID: " + userId));

        targetUser.deactivate();
        saveUserPort.save(targetUser);
    }

    @Override
    public UserResult getUserById(Long userId) {
        verifyStaffAccess();

        User user = findUserPort.findById(userId)
                .orElseThrow(() -> new DomainException("User not found with ID: " + userId));

        return mapToResult(user);
    }

    @Override
    public List<UserResult> getAllUsersByRole(String roleName) {
        verifyAdminAccess();

        List<User> users = findUserPort.findByRoleName(roleName);
        return users.stream().map(this::mapToResult).collect(Collectors.toList());
    }

    private void verifyAdminAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateAdminAccess(currentUser);
    }

    private void verifyStaffAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new UnauthenticatedException("User is unauthenticated");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }

    private UserResult mapToResult(User user) {
        Set<String> roleNames = user.getRoles()
                .stream()
                .filter(Objects::nonNull)
                .map(Role::getName)
                .collect(Collectors.toSet());

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