package org.example.librarymanagement.infrastructure.persistence.manage;

import java.util.Optional;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.port.outbound.manage.LoadRolePort;
import org.springframework.stereotype.Component;

@Component
public class RolePersistenceAdapter implements LoadRolePort {

    private final RoleJpaRepository roleJpaRepository;

    public RolePersistenceAdapter(RoleJpaRepository roleJpaRepository) {
        this.roleJpaRepository = roleJpaRepository;
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleJpaRepository.findByName(name)
                .map(entity -> new Role(entity.getId(), entity.getName(), entity.getDescription()));
    }
}
