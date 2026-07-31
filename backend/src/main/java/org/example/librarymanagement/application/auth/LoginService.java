package org.example.librarymanagement.application.auth;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.port.dtos.auth.LoginCommand;
import org.example.librarymanagement.port.dtos.auth.LoginResult;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.outbound.auth.LoadUserPort;
import org.example.librarymanagement.port.outbound.auth.PasswordVerifierPort;
import org.example.librarymanagement.port.outbound.auth.TokenProviderPort;

public class LoginService implements LoginUseCase {

    private final LoadUserPort loadUserPort;
    private final PasswordVerifierPort passwordVerifierPort;
    private final TokenProviderPort tokenProviderPort;

    public LoginService(
            LoadUserPort loadUserPort,
            PasswordVerifierPort passwordVerifierPort,
            TokenProviderPort tokenProviderPort
    ) {
        this.loadUserPort = Objects.requireNonNull(
                loadUserPort,
                "Load user port must not be null"
        );
        this.passwordVerifierPort = Objects.requireNonNull(
                passwordVerifierPort,
                "Password verifier port must not be null"
        );
        this.tokenProviderPort = Objects.requireNonNull(
                tokenProviderPort,
                "Token provider port must not be null"
        );
    }

    @Override
    public LoginResult login(LoginCommand command) {
        validateCommand(command);

        String username = command.username().trim();

        User user = loadUserPort
                .findByUsername(username)
                .orElseThrow(InvalidCredentialsException::new);

        user.ensureCanLogin();

        boolean passwordMatches = passwordVerifierPort.matches(
                command.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String accessToken =
                tokenProviderPort.generateAccessToken(user);

        Set<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toUnmodifiableSet());

        return new LoginResult(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                roleNames,
                accessToken
        );
    }

    private void validateCommand(LoginCommand command) {
        if (command == null) {
            throw new IllegalArgumentException(
                    "Login command must not be null"
            );
        }

        if (command.username() == null
                || command.username().isBlank()) {
            throw new IllegalArgumentException(
                    "Username must not be blank"
            );
        }

        if (command.password() == null
                || command.password().isBlank()) {
            throw new IllegalArgumentException(
                    "Password must not be blank"
            );
        }
    }
}
