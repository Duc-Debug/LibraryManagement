package org.example.librarymanagement.port.inbound.manage;

import java.util.List;

public interface ManageUserUseCase {

    UserResult createUser(CreateUserCommand command);

    UserResult updateUser(UpdateUserCommand command);

    void deactivateUser(Long userId); // Soft Delete

    UserResult getUserById(Long userId);

    List<UserResult> getAllUsersByRole(String roleName);
}
