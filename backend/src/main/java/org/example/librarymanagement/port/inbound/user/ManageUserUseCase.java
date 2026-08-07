package org.example.librarymanagement.port.inbound.user;

import java.util.List;

import org.example.librarymanagement.port.dtos.user.CreateUserCommand;
import org.example.librarymanagement.port.dtos.user.UpdateUserCommand;
import org.example.librarymanagement.port.dtos.user.UserResult;

public interface ManageUserUseCase {

    UserResult createUser(CreateUserCommand command);

    UserResult updateUser(UpdateUserCommand command);

    void deactivateUser(Long userId); // Soft Delete

    UserResult getUserById(Long userId);

    List<UserResult> getAllUsersByRole(String roleName);
}
