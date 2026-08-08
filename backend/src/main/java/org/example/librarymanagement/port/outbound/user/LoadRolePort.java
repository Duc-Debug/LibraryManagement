package org.example.librarymanagement.port.outbound.user;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Role;

public interface LoadRolePort {

    Optional<Role> findByName(String name);
}
