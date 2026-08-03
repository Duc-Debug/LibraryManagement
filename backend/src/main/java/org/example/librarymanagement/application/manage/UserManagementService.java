package org.example.librarymanagement.application.manage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.policies.AccountLockPolicy;
import org.example.librarymanagement.domain.policies.AuthorizationAccessPolicy;
import org.example.librarymanagement.domain.policies.UniqueUsernamePolicy;
import org.example.librarymanagement.port.inbound.manage.CreateUserCommand;
import org.example.librarymanagement.port.inbound.manage.ManageUserUseCase;
import org.example.librarymanagement.port.inbound.manage.UpdateUserCommand;
import org.example.librarymanagement.port.inbound.manage.UserResult;
import org.example.librarymanagement.port.outbound.admin.EncodePasswordPort;
import org.example.librarymanagement.port.outbound.manage.FindUserPort;
import org.example.librarymanagement.port.outbound.manage.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.manage.LoadRolePort;
import org.example.librarymanagement.port.outbound.manage.SaveUserPort;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserManagementService implements ManageUserUseCase {

    private final FindUserPort findUserPort;
    private final LoadRolePort loadRolePort;
    private final SaveUserPort saveUserPort;
    private final EncodePasswordPort encodePasswordPort;
    private final GetAuthenticatedUserPort getAuthenticatedUserPort;

    @Override
    public UserResult createUser(CreateUserCommand command) {
        verifyAdminAccess();

        boolean isUsernameExisted = findUserPort.existsByUsername(command.username());
        UniqueUsernamePolicy.validateUsernameForCreate(isUsernameExisted, command.username());

        Role role = loadRolePort.findByName(command.roleName())
                .orElseThrow(() -> new DomainException("Hệ thống không tồn tại quyền: " + command.roleName()));

        String encodedPassword = encodePasswordPort.encode(command.rawPassword());

        User newUser = new User(command.username(), encodedPassword, command.fullName(), command.email(), command.phone());
        newUser.addRole(role);

        User savedUser = saveUserPort.save(newUser);
        return mapToResult(savedUser);
    }

    @Override
    public UserResult updateUser(UpdateUserCommand command) {
        verifyAdminAccess();

        User targetUser = findUserPort.findById(command.userId())
                .orElseThrow(() -> new DomainException("Không tìm thấy dữ liệu người dùng cần cập nhật."));

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
                .orElseThrow(() -> new DomainException("Không tìm thấy dữ liệu người dùng để vô hiệu hóa."));
        targetUser.deactivate();
        saveUserPort.save(targetUser);
    }

    @Override
    public UserResult getUserById(Long userId) {
        verifyStaffAccess();
        User user = findUserPort.findById(userId)
                .orElseThrow(() -> new DomainException("Không tìm thấy dữ liệu người dùng."));
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
            throw new DomainException("Lỗi bảo mật: Không thể xác định danh tính.");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateAdminAccess(currentUser);
    }

    private void verifyStaffAccess() {
        User currentUser = getAuthenticatedUserPort.getCurrentUser();
        if (currentUser == null) {
            throw new DomainException("Lỗi bảo mật: Không thể xác định danh tính.");
        }
        AccountLockPolicy.validateAccountActive(currentUser);
        AuthorizationAccessPolicy.validateStaffAccess(currentUser);
    }

    private UserResult mapToResult(User user) {
        Set<String> roleNames = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        return new UserResult(user.getId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone(), user.isEnabled(), roleNames, user.getCreatedAt(), user.getUpdatedAt());
    }
}
