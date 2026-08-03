package org.example.librarymanagement.infrastructure.web.auth;

import jakarta.validation.Valid;
import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutResult;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private final LogoutUseCase logoutUseCase;

    // CONSTRUCTOR BẮT BUỘC ĐỂ KHỎI BỊ LỖI COMPILER:
    public LogoutController(LogoutUseCase logoutUseCase) {
        this.logoutUseCase = logoutUseCase;
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(
            @Valid @RequestBody LogoutRequest request
    ) {
        LogoutCommand command = new LogoutCommand(request.token());
        LogoutResult result = logoutUseCase.logout(command);

        LogoutResponse response = new LogoutResponse(
                result.success(),
                result.message()
        );

        return ResponseEntity.ok(response);
    }
}