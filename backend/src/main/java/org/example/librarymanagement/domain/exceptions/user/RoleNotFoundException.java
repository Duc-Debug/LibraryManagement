package org.example.librarymanagement.domain.exceptions.user;

import org.example.librarymanagement.domain.exceptions.DomainException;

public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException(String roleName) {
        super("Role not found: " + roleName);
    }
}
