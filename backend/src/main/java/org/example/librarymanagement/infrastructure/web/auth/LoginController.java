package org.example.librarymanagement.infrastructure.web.auth;
import org.example.librarymanagement.port.dtos.auth.LoginCommand;
import org.example.librarymanagement.port.dtos.auth.LoginResult;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final LoginUseCase loginUseCase;

    public LoginController(
            LoginUseCase loginUseCase
    ) {
        this.loginUseCase = loginUseCase;
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

}
