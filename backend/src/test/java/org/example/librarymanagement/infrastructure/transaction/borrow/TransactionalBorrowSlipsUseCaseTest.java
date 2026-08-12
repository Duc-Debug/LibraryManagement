package org.example.librarymanagement.infrastructure.transaction.borrow;

import java.util.List;

import org.example.librarymanagement.port.dtos.borrow.BorrowSlipFilterQuery;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.borrow.BorrowSlipsUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TransactionalBorrowSlipsUseCaseTest {

    @Mock
    private BorrowSlipsUseCase delegate;

    @Test
    @DisplayName("TransactionalBorrowSlipsUseCase: Ném NullPointerException khi delegate null")
    void constructor_NullDelegate_ThrowsException() {
        assertThrows(NullPointerException.class, () -> new TransactionalBorrowSlipsUseCase(null));
    }

    @Test
    @DisplayName("getBorrowSlips: Ủy quyền chính xác sang delegate")
    void getBorrowSlips_DelegatesProperly() {
        // Arrange
        TransactionalBorrowSlipsUseCase useCase = new TransactionalBorrowSlipsUseCase(delegate);
        BorrowSlipFilterQuery query = new BorrowSlipFilterQuery(0, 10, null, null);
        PageResult<BorrowSlipResponseDto> mockResult = new PageResult<>(List.of(), 0, 10, 0L, 0);

        when(delegate.getBorrowSlips(query)).thenReturn(mockResult);

        // Act
        PageResult<BorrowSlipResponseDto> result = useCase.getBorrowSlips(query);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.content().size());
        verify(delegate).getBorrowSlips(query);
    }
}
