package org.example.librarymanagement.application.borrow;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.example.librarymanagement.application.shared.ValidationException;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBorrowSlipsServiceTest {
    @Mock
    private LoadBorrowSlipPort loadBorrowSlipPort;
    @Mock
    private GetAuthenticatedUserPort getAuthenticatedUserPort;
    private GetBorrowSlipsService getBorrowSlipsService;

      @BeforeEach
    public void setUp() {
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
    @DisplayName("getBorrowSlips: Cắt khoảng trắng thừa của keyword khi query hợp lệ")
    void getBorrowSlips_TrimKeyword() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        PageResult<BorrowSlipResponseDto> mockEmptyPage = new PageResult<>(List.of(), 0, 10, 0L, 0);
        when(loadBorrowSlipPort.findBorrowSlips(0, 10, null, "test"))
                .thenReturn(mockEmptyPage);
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 10, null, "  test  ");

        // Act
        PageResult<BorrowSlipResponseDto> result = getBorrowSlipsService.getBorrowSlips(query);

        // Assert
        assertNotNull(result);
        verify(loadBorrowSlipPort).findBorrowSlips(0, 10, null, "test");
    }

    @Test
    @DisplayName("getBorrowSlips: Ném ValidationException khi query là null")
    void getBorrowSlips_ThrowsValidationException_WhenQueryNull() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> getBorrowSlipsService.getBorrowSlips(null)
        );
        assertEquals("Filter query must not be null", exception.getMessage());
    }

    @Test
    @DisplayName("getBorrowSlips: Ném ValidationException khi page < 0")
    void getBorrowSlips_ThrowsValidationException_WhenPageNegative() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(-1, 10, null, null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> getBorrowSlipsService.getBorrowSlips(query)
        );
        assertEquals("Page index must be greater than or equal to 0", exception.getMessage());
    }

    @Test
    @DisplayName("getBorrowSlips: Ném ValidationException khi size <= 0")
    void getBorrowSlips_ThrowsValidationException_WhenSizeZeroOrNegative() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());

        BorrowSlipFilterQuery queryZero = new BorrowSlipFilterQuery(0, 0, null, null);
        ValidationException exZero = assertThrows(
                ValidationException.class,
                () -> getBorrowSlipsService.getBorrowSlips(queryZero)
        );
        assertEquals("Page size must be greater than 0", exZero.getMessage());

        BorrowSlipFilterQuery queryNegative = new BorrowSlipFilterQuery(0, -5, null, null);
        ValidationException exNeg = assertThrows(
                ValidationException.class,
                () -> getBorrowSlipsService.getBorrowSlips(queryNegative)
        );
        assertEquals("Page size must be greater than 0", exNeg.getMessage());
    }

    @Test
    @DisplayName("getBorrowSlips: Ném ValidationException khi size > 100")
    void getBorrowSlips_ThrowsValidationException_WhenSizeExceedsMax() {
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createLibrarianUser());
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 5000, null, null);
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> getBorrowSlipsService.getBorrowSlips(query)
        );
        assertEquals("Page size must not exceed " + GetBorrowSlipsService.MAX_PAGE_SIZE, exception.getMessage());
    }

    @Test
    @DisplayName("getBorrowSlips: Ném UnauthenticatedException khi chưa đăng nhập")
    void getBorrowSlips_ThrowsUnauthenticatedException() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(null);
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 10, null, null);
        // Act & Assert
        UnauthenticatedException exception = assertThrows(
                UnauthenticatedException.class,
                () -> getBorrowSlipsService.getBorrowSlips(query)
        );
        assertNotNull(exception.getMessage());
        verify(loadBorrowSlipPort, never()).findBorrowSlips(anyInt(), anyInt(), any(), any());
    }

    @Test
    @DisplayName("getBorrowSlips: Ném AccessDeniedException khi tài khoản không phải là Thủ thư/Admin")
    void getBorrowSlips_ThrowsAccessDeniedException() {
        // Arrange
        when(getAuthenticatedUserPort.getCurrentUser()).thenReturn(createReaderUser());
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 10, null, null);
        // Act & Assert
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> getBorrowSlipsService.getBorrowSlips(query)
        );
        assertNotNull(exception.getMessage());
        verify(loadBorrowSlipPort, never()).findBorrowSlips(anyInt(), anyInt(), any(), any());
    }
}
