package org.example.librarymanagement.infrastructure.web.auth;

import org.example.librarymanagement.infrastructure.web.ErrorResponse;
import org.example.librarymanagement.port.inbound.auth.LoginCommand;
import org.example.librarymanagement.port.inbound.auth.LoginResult;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
    public ResponseEntity<?> logout(
            @RequestHeader(
                    value = "Authorization",
                    required = false
            ) String authorizationHeader
    ) {
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            ErrorResponse errorResponse = ErrorResponse.of(
                    "validation",
                    "Invalid Authorization header. Expected format: 'Bearer <token>'"
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        }

        String token = authorizationHeader
                .substring(7)
                .trim();

        if (token.isBlank()) {
            ErrorResponse errorResponse = ErrorResponse.of(
                    "validation",
                    "Bearer token cannot be empty."
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(errorResponse);
        }
        LogoutCommand command = new LogoutCommand(token);  
        logoutUseCase.logout(command);
        return ResponseEntity.noContent().build();
    }
}