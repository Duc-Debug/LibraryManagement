package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutResult;
import org.example.librarymanagement.port.outbound.auth.TokenBlacklistPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Tự động khởi tạo Mockito annotations
class LogoutServiceTest {

    @Mock
    private TokenBlacklistPort tokenBlacklistPort; // Mock outbound port

    @InjectMocks
    private LogoutService logoutService; // Service cần test

    // =========================================================================
    // 1. SUCCESS SCENARIOS
    // =========================================================================

    @Test
    @DisplayName("Thành công: Đăng xuất và đưa token vào blacklist thành công")
    void logout_WithValidToken_ShouldReturnSuccessResult() {
        // Arrange
        String validToken = "valid.jwt.token.here";
        LogoutCommand command = new LogoutCommand(validToken);

        // Act
        LogoutResult result = logoutService.logout(command);

        // Assert
        assertTrue(result.success());
        assertEquals("Logout successful.", result.message());

        // Verify: Đảm bảo Outbound Port được gọi đúng 1 lần với đúng token
        verify(tokenBlacklistPort).blacklistToken(validToken);
    }

    // =========================================================================
    // 2. INVALID INPUT SCENARIOS
    // =========================================================================

    @Test
    @DisplayName("Thất bại: Token bị null")
    void logout_WithNullToken_ShouldReturnFailureResult() {
        // Arrange
        LogoutCommand command = new LogoutCommand(null);

        // Act
        LogoutResult result = logoutService.logout(command);

        // Assert
        assertFalse(result.success());
        assertEquals("Token cannot be null or empty.", result.message());

        // Verify: Đảm bảo KHÔNG gọi xuống TokenBlacklistPort khi token bị null
        verify(tokenBlacklistPort, never()).blacklistToken(anyString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Thất bại: Token rỗng hoặc chỉ chứa khoảng trắng")
    void logout_WithEmptyOrBlankToken_ShouldReturnFailureResult(String invalidToken) {
        // Arrange
        LogoutCommand command = new LogoutCommand(invalidToken);

        // Act
        LogoutResult result = logoutService.logout(command);

        // Assert
        assertFalse(result.success());
        assertEquals("Token cannot be null or empty.", result.message());

        // Verify: Không gọi xuống Outbound Port
        verify(tokenBlacklistPort, never()).blacklistToken(anyString());
    }

    // =========================================================================
    // 3. EXCEPTION HANDLING SCENARIOS
    // =========================================================================

    @Test
    @DisplayName("Thất bại: Outbound Port bị lỗi (Ném ra Exception khi ghi Redis/DB)")
    void logout_WhenTokenBlacklistPortThrowsException_ShouldCatchAndReturnFailureResult() {
        // Arrange
        String validToken = "valid.jwt.token";
        LogoutCommand command = new LogoutCommand(validToken);

        // Giả lập TokenBlacklistPort bị ném Exception (VD: Mất kết nối Redis)
        willThrow(new RuntimeException("Redis connection refused"))
                .given(tokenBlacklistPort).blacklistToken(validToken);

        // Act
        LogoutResult result = logoutService.logout(command);

        // Assert
        assertFalse(result.success());
        assertEquals("Logout failed due to an internal error.", result.message());

        // Verify
        verify(tokenBlacklistPort).blacklistToken(validToken);
    }
}