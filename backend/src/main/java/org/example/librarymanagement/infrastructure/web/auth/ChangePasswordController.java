package org.example.librarymanagement.infrastructure.web.auth;

import org.example.librarymanagement.infrastructure.web.auth.dtos.ChangePasswordRequest;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
public class ChangePasswordController {
    private final ChangePasswordUseCase changePasswordUseCase;

    public ChangePasswordController(ChangePasswordUseCase changePasswordUseCase){
        this.changePasswordUseCase = changePasswordUseCase;
    }
    @PostMapping("/change-password")
    public ResponseEntity<ChangePasswordResult> changePassword(
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId, 
            @Valid @RequestBody ChangePasswordRequest request
    ){
        ChangePasswordCommand command = new ChangePasswordCommand(
            userId,
            request.oldPassword(),
            request.newPassword(),
            request.confirmPassword()
        );
        ChangePasswordResult result = changePasswordUseCase.changePassword(command);
        if(!result.success()){
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}
