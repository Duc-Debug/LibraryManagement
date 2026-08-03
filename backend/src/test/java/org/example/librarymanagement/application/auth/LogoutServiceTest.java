package org.example.librarymanagement.application.auth;

import org.example.librarymanagement.port.inbound.auth.LogoutCommand;
import org.example.librarymanagement.port.inbound.auth.LogoutResult;
import org.example.librarymanagement.port.outbound.auth.TokenBlacklistPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private TokenBlacklistPort tokenBlacklistPort;

    @InjectMocks
    private LogoutService logoutService;

    @Test
    @DisplayName("Đăng xuất thành công khi Token hợp lệ")
    void logout_ShouldReturnSuccess_WhenTokenIsValid() {
        // Given
        String validToken = "valid.jwt.token";
        LogoutCommand command = new LogoutCommand(validToken);

        // When
        LogoutResult result = logoutService.logout(command);

        // Then
        assertTrue(result.success());
        assertEquals("Logout successful.", result.message());
        
        // Xác nhận tokenBlacklistPort đã được gọi đúng 1 lần
        verify(tokenBlacklistPort, times(1)).blacklistToken(validToken);
    }

    @Test
    @DisplayName("Đăng xuất thất bại khi Token bị null hoặc khoảng trắng")
    void logout_ShouldReturnFailure_WhenTokenIsNullOrEmpty() {
        // Given
        LogoutCommand nullTokenCommand = new LogoutCommand(null);
        LogoutCommand emptyTokenCommand = new LogoutCommand("   ");

        // When
        LogoutResult nullResult = logoutService.logout(nullTokenCommand);
        LogoutResult emptyResult = logoutService.logout(emptyTokenCommand);

        // Then
        assertFalse(nullResult.success());
        assertEquals("Token cannot be null or empty.", nullResult.message());

        assertFalse(emptyResult.success());
        assertEquals("Token cannot be null or empty.", emptyResult.message());

        // Đảm bảo không gọi tới TokenBlacklistPort khi Token không hợp lệ
        verifyNoInteractions(tokenBlacklistPort);
    }

    @Test
    @DisplayName("Đăng xuất thất bại khi TokenBlacklistPort ném ra Exception")
    void logout_ShouldReturnFailure_WhenPortThrowsException() {
        // Given
        String validToken = "valid.jwt.token";
        LogoutCommand command = new LogoutCommand(validToken);

        // Giả lập TokenBlacklistPort bị lỗi (ví dụ: mất kết nối Database/Redis)
        doThrow(new RuntimeException("Database error"))
                .when(tokenBlacklistPort).blacklistToken(validToken);

        // When
        LogoutResult result = logoutService.logout(command);

        // Then
        assertFalse(result.success());
        assertEquals("Logout failed due to an internal error.", result.message());
        
        verify(tokenBlacklistPort, times(1)).blacklistToken(validToken);
    }
}