package org.example.librarymanagement.infrastructure.web.auth;
import jakarta.validation.Valid;
import org.example.librarymanagement.port.inbound.auth.LoginCommand;
import org.example.librarymanagement.port.inbound.auth.LoginResult;
import org.example.librarymanagement.port.inbound.auth.LoginUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<LoginResult> login(
            @Valid
            @RequestBody
            LoginRequest request
    ) {

        LoginCommand command =
                new LoginCommand(
                        request.username(),
                        request.password()
                );

        LoginResult result =
                loginUseCase.login(command);

        return ResponseEntity.ok(result);

    }

}
