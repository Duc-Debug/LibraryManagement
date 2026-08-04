package org.example.librarymanagement.infrastructure.web.auth;

import org.example.librarymanagement.application.auth.InvalidCredentialsException;
import org.example.librarymanagement.infrastructure.security.UserPrincipal;
import org.example.librarymanagement.infrastructure.web.auth.dtos.UpdateProfileRequest;
import org.example.librarymanagement.port.dtos.auth.UpdateProfileCommand;
import org.example.librarymanagement.port.inbound.auth.ProfileUseCase;
import org.example.librarymanagement.port.inbound.manage.UserResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserProfileController {

    private final ProfileUseCase profileUseCase;

    @GetMapping("/me")
    public ResponseEntity<UserResult> getMyProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            throw new InvalidCredentialsException("Yêu cầu xác thực token bất thành hoặc phiên làm việc đã hết hạn.");
        }
        UserResult profile = profileUseCase.getProfile(principal.getId());
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @Transactional
    public ResponseEntity<UserResult> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        if (principal == null) {
            throw new InvalidCredentialsException("Yêu cầu xác thực token bất thành hoặc phiên làm việc đã hết hạn.");
        }
        UpdateProfileCommand command = new UpdateProfileCommand(
                request.fullName(),
                request.email(),
                request.phone()
        );
        UserResult updated = profileUseCase.updateProfile(principal.getId(), command);
        return ResponseEntity.ok(updated);
    }
}
