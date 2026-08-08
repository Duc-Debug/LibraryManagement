package org.example.librarymanagement.infrastructure.web.auth;

import org.example.librarymanagement.domain.exceptions.shared.InvalidCredentialsException;
import org.example.librarymanagement.infrastructure.security.UserPrincipal;
import org.example.librarymanagement.infrastructure.web.auth.dtos.UpdateProfileRequest;
import org.example.librarymanagement.port.dtos.auth.UpdateProfileCommand;
import org.example.librarymanagement.port.dtos.user.UserResult;
import org.example.librarymanagement.port.inbound.auth.ProfileUseCase;
import org.example.librarymanagement.port.outbound.auth.token.VerifiedAccessToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<UserResult> getMyProfile(@AuthenticationPrincipal Object principal) {
        if (principal == null) {
            throw new InvalidCredentialsException("Authentication token verification failed or session expired");
        }
        Long userId = extractUserId(principal);
        UserResult profile = profileUseCase.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResult> updateMyProfile(
            @AuthenticationPrincipal Object principal,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        if (principal == null) {
            throw new InvalidCredentialsException("Authentication token verification failed or session expired");
        }
        Long userId = extractUserId(principal);
        UpdateProfileCommand command = new UpdateProfileCommand(
                request.fullName(),
                request.email(),
                request.phone()
        );
        UserResult updated = profileUseCase.updateProfile(userId, command);
        return ResponseEntity.ok(updated);
    }

    private Long extractUserId(Object principal) {
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        if (principal instanceof VerifiedAccessToken verifiedToken) {
            return verifiedToken.userId();
        }
        throw new InvalidCredentialsException("Authentication token verification failed or session expired");
    }
}
