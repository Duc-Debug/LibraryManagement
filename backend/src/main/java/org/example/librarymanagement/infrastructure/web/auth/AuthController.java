package org.example.librarymanagement.infrastructure.web.auth;

import jakarta.validation.Valid;
import org.example.librarymanagement.infrastructure.web.auth.dtos.LoginRequest;
import org.example.librarymanagement.infrastructure.web.auth.dtos.LoginResponse;
import org.example.librarymanagement.port.dtos.auth.LoginCommand;
import org.example.librarymanagement.port.dtos.auth.LoginResult;
import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;

    public AuthController(
            LoginUseCase loginUseCase,
            LogoutUseCase logoutUseCase
    ) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginCommand command = new LoginCommand(
                request.username(),
                request.password()
        );

        LoginResult result = loginUseCase.login(command);

        LoginResponse response = new LoginResponse(
                result.userId(),
                result.username(),
                result.fullName(),
                result.roles(),
                result.accessToken(),
                "Bearer"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        String token = extractBearerToken(authorizationHeader);
        LogoutCommand command = new LogoutCommand(token);

        logoutUseCase.logout(command);

        return ResponseEntity.noContent().build();
    }

    private String extractBearerToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new InvalidAuthorizationHeaderException(
                    "Invalid Authorization header. Expected format: 'Bearer <token>'"
            );
        }

        String token = authorizationHeader
                .substring(BEARER_PREFIX.length())
                .trim();

        if (token.isBlank()) {
            throw new InvalidAuthorizationHeaderException(
                    "Bearer token cannot be empty."
            );
        }

        return token;
    }
}
