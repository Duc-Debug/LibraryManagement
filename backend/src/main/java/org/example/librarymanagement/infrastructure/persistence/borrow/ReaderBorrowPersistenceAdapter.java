package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.port.dtos.borrow.OverdueSlipSummaryDto;
import org.example.librarymanagement.port.outbound.borrow.LoadReaderBorrowStatusPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ReaderBorrowPersistenceAdapter implements LoadReaderBorrowStatusPort {

    private static final String COUNT_CURRENT_BORROWING_SQL = """
            SELECT COUNT(bd.id)
            FROM borrow_details bd
            JOIN borrow_slips bs ON bd.borrow_slip_id = bs.id
            WHERE bs.reader_id = ?
              AND bd.returned_at IS NULL
              AND bs.status IN ('BORROWING', 'OVERDUE')
            """;

    private static final String HAS_OVERDUE_BOOKS_SQL = """
            SELECT EXISTS (
                SELECT 1
                FROM borrow_slips bs
                JOIN borrow_details bd ON bd.borrow_slip_id = bs.id
                WHERE bs.reader_id = ?
                  AND bd.returned_at IS NULL
                  AND (bs.status = 'OVERDUE' OR DATE(bs.due_at) < DATE(?))
            )
            """;

    private static final String FIND_OVERDUE_SLIPS_SQL = """
            SELECT 
                bs.id AS slip_id,
                bs.borrow_code AS borrow_code,
                bs.due_at AS due_at,
                COUNT(bd.id) AS unreturned_books_count
            FROM borrow_slips bs
            JOIN borrow_details bd ON bd.borrow_slip_id = bs.id
            WHERE bs.reader_id = ?
              AND bd.returned_at IS NULL
              AND (bs.status = 'OVERDUE' OR DATE(bs.due_at) < DATE(?))
            GROUP BY bs.id, bs.borrow_code, bs.due_at
            HAVING COUNT(bd.id) > 0
            """;

    private final JdbcTemplate jdbcTemplate;

    public ReaderBorrowPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "JdbcTemplate must not be null");
    }

    @Override
    public int countCurrentBorrowingBooks(Long readerId) {
        if (readerId == null || readerId <= 0) {
            return 0;
        }
        Integer count = jdbcTemplate.queryForObject(
                COUNT_CURRENT_BORROWING_SQL,
                Integer.class,
                readerId
        );
        return count != null ? count : 0;
    }

    @Override
    public boolean hasOverdueBooks(Long readerId, LocalDateTime asOfDate) {
        if (readerId == null || readerId <= 0) {
            return false;
        }
        LocalDateTime effectiveDate = asOfDate != null ? asOfDate : LocalDateTime.now();
        Boolean result = jdbcTemplate.queryForObject(
                HAS_OVERDUE_BOOKS_SQL,
                Boolean.class,
                readerId,
                Timestamp.valueOf(effectiveDate)
        );
        return Boolean.TRUE.equals(result);
    }

    @Override
    public List<OverdueSlipSummaryDto> findOverdueSlipsByReaderId(Long readerId, LocalDateTime asOfDate) {
        if (readerId == null || readerId <= 0) {
            return List.of();
        }
        LocalDateTime effectiveDate = asOfDate != null ? asOfDate : LocalDateTime.now();

        return jdbcTemplate.query(
                FIND_OVERDUE_SLIPS_SQL,
                (rs, rowNum) -> {
                    Long slipId = rs.getLong("slip_id");
                    String borrowCode = rs.getString("borrow_code");
                    Timestamp dueAtTimestamp = rs.getTimestamp("due_at");
                    LocalDateTime dueAt = dueAtTimestamp != null ? dueAtTimestamp.toLocalDateTime() : effectiveDate;
                    int unreturnedCount = rs.getInt("unreturned_books_count");

                    long overdueDays = Math.max(1, ChronoUnit.DAYS.between(dueAt.toLocalDate(), effectiveDate.toLocalDate()));

                    return new OverdueSlipSummaryDto(
                            slipId,
                            borrowCode,
                            dueAt,
                            overdueDays,
                            unreturnedCount
                    );
                },
                readerId,
                Timestamp.valueOf(effectiveDate)
        );
    }
}
