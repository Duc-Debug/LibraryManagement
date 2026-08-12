package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.port.dtos.borrow.OverdueSlipSummaryDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

@ExtendWith(MockitoExtension.class)
class ReaderBorrowPersistenceAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ReaderBorrowPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ReaderBorrowPersistenceAdapter(jdbcTemplate);
    }

    @Test
    @DisplayName("countCurrentBorrowingBooks - Return count from JdbcTemplate")
    void countCurrentBorrowingBooks_Success() {
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq(10L))).thenReturn(3);

        int count = adapter.countCurrentBorrowingBooks(10L);

        assertEquals(3, count);
        verify(jdbcTemplate).queryForObject(anyString(), eq(Integer.class), eq(10L));
    }

    @Test
    @DisplayName("countCurrentBorrowingBooks - Return 0 when readerId is invalid")
    void countCurrentBorrowingBooks_InvalidReaderId() {
        assertEquals(0, adapter.countCurrentBorrowingBooks(null));
        assertEquals(0, adapter.countCurrentBorrowingBooks(0L));
        assertEquals(0, adapter.countCurrentBorrowingBooks(-1L));
    }

    @Test
    @DisplayName("hasOverdueBooks - Return boolean result from JdbcTemplate")
    void hasOverdueBooks_Success() {
        LocalDateTime now = LocalDateTime.now();
        when(jdbcTemplate.queryForObject(anyString(), eq(Boolean.class), eq(10L), any(Timestamp.class)))
                .thenReturn(true);

        boolean hasOverdue = adapter.hasOverdueBooks(10L, now);

        assertTrue(hasOverdue);
    }

    @Test
    @DisplayName("hasOverdueBooks - Return false when readerId is invalid")
    void hasOverdueBooks_InvalidReaderId() {
        assertFalse(adapter.hasOverdueBooks(null, LocalDateTime.now()));
        assertFalse(adapter.hasOverdueBooks(0L, LocalDateTime.now()));
    }

    @Test
    @DisplayName("findOverdueSlipsByReaderId - Return list from JdbcTemplate")
    void findOverdueSlipsByReaderId_Success() {
        LocalDateTime now = LocalDateTime.now();
        OverdueSlipSummaryDto dto = new OverdueSlipSummaryDto(1L, "PM-001", now.minusDays(3), 3L, 2);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(10L), any(Timestamp.class)))
                .thenReturn(List.of(dto));

        List<OverdueSlipSummaryDto> result = adapter.findOverdueSlipsByReaderId(10L, now);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("PM-001", result.get(0).borrowCode());
    }

    @Test
    @DisplayName("findOverdueSlipsByReaderId - Return empty list when readerId is invalid")
    void findOverdueSlipsByReaderId_InvalidReaderId() {
        List<OverdueSlipSummaryDto> result = adapter.findOverdueSlipsByReaderId(null, LocalDateTime.now());
        assertTrue(result.isEmpty());
    }
}
