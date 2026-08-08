package org.example.librarymanagement.infrastructure.security;

import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.example.librarymanagement.port.outbound.user.LoadUserPort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SpringSecurityAuthenticatedUserAdapter implements GetAuthenticatedUserPort {

    private final LoadUserPort loadUserPort;

    public SpringSecurityAuthenticatedUserAdapter(LoadUserPort loadUserPort) {
        this.loadUserPort = loadUserPort;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return (User) principal;
        }

        if (principal instanceof UserPrincipal userPrincipal) {
            return loadUserPort.findByUsername(userPrincipal.getUsername()).orElse(null);
        }

        if (principal instanceof VerifiedAccessToken verifiedAccessToken) {
            return loadUserPort.findByUsername(verifiedAccessToken.username()).orElse(null);
        }

        if (principal instanceof String username) {
            return loadUserPort.findByUsername(username).orElse(null);
        }

        return null;
    }
}


