package org.example.librarymanagement.application.borrow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.example.librarymanagement.domain.entity.Role;
import org.example.librarymanagement.domain.entity.User;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.domain.exceptions.shared.AccessDeniedException;
import org.example.librarymanagement.domain.exceptions.shared.UnauthenticatedException;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.outbound.borrow.LoadBorrowSlipPort;
import org.example.librarymanagement.port.outbound.user.GetAuthenticatedUserPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetBorrowSlipsServiceTest {
    @Mock
    private LoadBorrowSlipPort loadBorrowSlipPort;
    @Mock
    private GetAuthenticatedUserPort getAuthenticatedUserPort;
    private GetBorrowSlipsService getBorrowSlipsService;

      @BeforeEach
    void setUp() {
        getBorrowSlipsService = new GetBorrowSlipsService(loadBorrowSlipPort, getAuthenticatedUserPort);
    }

     private User createLibrarianUser() {
        Role librarianRole = new Role(2L, "LIBRARIAN", "Librarian Role");
        return new User(
                1L,
                "librarian1",
                "hashedPwd",
                "Thủ thư A",
                "lib@test.com",
                "0901234567",
                true,
                null,                   // passwordChangedAt
                LocalDateTime.now(),    // createdAt
                LocalDateTime.now(),    // updatedAt
                Set.of(librarianRole)   // roles (truyền ở cuối cùng)
        );
    }

    private User createReaderUser() {
        Role readerRole = new Role(3L, "READER", "Reader Role");
        return new User(
                2L,
                "reader1",
                "hashedPwd",
                "Độc giả B",
                "reader@test.com",
                "0901234568",
                true,
                null,                   // passwordChangedAt
                LocalDateTime.now(),    // createdAt
                LocalDateTime.now(),    // updatedAt
                Set.of(readerRole)      // roles (truyền ở cuối cùng)
        );
    }
     private BorrowSlipResponseDto createSampleSlipDto(Long id, String code, BorrowSlipStatus status) {
        return new BorrowSlipResponseDto(
                id,
                code,
                10L,
                "CARD-001",
                "Độc giả A",
                1L,
                "Thủ thư A",
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(11),
                status,
                null,
                2,
                LocalDateTime.now().minusDays(3)
        );
    }

     @Test
    @DisplayName("getBorrowSlips: Lấy danh sách phiếu mượn thành công theo trạng thái BORROWING")
    void getBorrowSlips_Success_WithStatusFilter() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        BorrowSlipResponseDto item1 = createSampleSlipDto(1L, "PM001", BorrowSlipStatus.BORROWING);
        PageResult<BorrowSlipResponseDto> mockPage = new PageResult<>(List.of(item1), 0, 10, 1L, 1);
        when(loadBorrowSlipPort.findBorrowSlips(0, 10, BorrowSlipStatus.BORROWING, "PM001"))
                .thenReturn(mockPage);
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 10, BorrowSlipStatus.BORROWING, "PM001");
        // Act
        PageResult<BorrowSlipResponseDto> result = getBorrowSlipsService.getBorrowSlips(query);
        // Assert
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals("PM001", result.content().get(0).borrowCode());
        assertEquals(BorrowSlipStatus.BORROWING, result.content().get(0).status());
        verify(loadBorrowSlipPort).findBorrowSlips(0, 10, BorrowSlipStatus.BORROWING, "PM001");
    }
    @Test
    @DisplayName("getBorrowSlips: Tự động chuẩn hóa tham số page âm, size <= 0 và keyword có khoảng trắng")
    void getBorrowSlips_NormalizeParameters() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        PageResult<BorrowSlipResponseDto> mockEmptyPage = new PageResult<>(List.of(), 0, 10, 0L, 0);
        when(loadBorrowSlipPort.findBorrowSlips(0, 10, null, "test"))
                .thenReturn(mockEmptyPage);
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(-2, -5, null, "  test  ");
        // Act
        PageResult<BorrowSlipResponseDto> result = getBorrowSlipsService.getBorrowSlips(query);
        // Assert
        assertNotNull(result);
        verify(loadBorrowSlipPort).findBorrowSlips(0, 10, null, "test");
    }
    @Test
    @DisplayName("getBorrowSlips: Ném UnauthenticatedException khi chưa đăng nhập")
    void getBorrowSlips_ThrowsUnauthenticatedException() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(null);
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 10, null, null);
        // Act & Assert
        assertThrows(UnauthenticatedException.class, () -> getBorrowSlipsService.getBorrowSlips(query));
        verify(loadBorrowSlipPort, never()).findBorrowSlips(anyInt(), anyInt(), any(), any());
    }
    @Test
    @DisplayName("getBorrowSlips: Ném AccessDeniedException khi tài khoản không phải là Thủ thư/Admin")
    void getBorrowSlips_ThrowsAccessDeniedException() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createReaderUser());
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 10, null, null);
        // Act & Assert
        assertThrows(AccessDeniedException.class, () -> getBorrowSlipsService.getBorrowSlips(query));
        verify(loadBorrowSlipPort, never()).findBorrowSlips(anyInt(), anyInt(), any(), any());
    }
}
