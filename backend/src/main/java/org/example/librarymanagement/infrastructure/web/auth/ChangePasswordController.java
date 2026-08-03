package org.example.librarymanagement.infrastructure.web.auth;

import org.example.librarymanagement.infrastructure.security.UserPrincipal;
import org.example.librarymanagement.infrastructure.web.auth.dtos.ChangePasswordRequest;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordCommand;
import org.example.librarymanagement.port.dtos.auth.ChangePasswordResult;
import org.example.librarymanagement.port.inbound.auth.ChangePasswordUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ChangePasswordController {
    private final ChangePasswordUseCase changePasswordUseCase;

    @PostMapping("/change-password")
    @Transactional
    public ResponseEntity<ChangePasswordResult> changePassword(
       @AuthenticationPrincipal UserPrincipal principal, 
            @Valid @RequestBody ChangePasswordRequest request
    ){
        ChangePasswordCommand command = new ChangePasswordCommand(
                principal.getId(), // 👈 Dùng ID của user đã đăng nhập
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
