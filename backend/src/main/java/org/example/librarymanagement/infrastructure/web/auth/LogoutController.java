package org.example.librarymanagement.infrastructure.web.auth;


import lombok.RequiredArgsConstructor;

import org.example.librarymanagement.infrastructure.web.exception.ErrorResponse;
import org.example.librarymanagement.port.dtos.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final LogoutUseCase logoutUseCase;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        // 1. Kiểm tra Authorization Header có hợp lệ không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.of("validation", "Invalid Authorization header. Expected format: 'Bearer <token>'"));
                    
        }

        // 2. Trích xuất token (bỏ chữ "Bearer " 7 ký tự đầu)
        String token = authHeader.substring(7).trim();

        // 3. Đóng gói vào LogoutCommand và gọi UseCase xử lý
        LogoutCommand command = new LogoutCommand(token);
        logoutUseCase.logout(command);

        // 4. Trả về kết quả cho Frontend
       return ResponseEntity.ok().build();
    }
}