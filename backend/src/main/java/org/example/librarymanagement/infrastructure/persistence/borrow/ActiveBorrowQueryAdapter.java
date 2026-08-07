package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.util.Objects;

import org.example.librarymanagement.port.outbound.borrow.CheckActiveReaderBorrowPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ActiveBorrowQueryAdapter
        implements CheckActiveReaderBorrowPort {

    private static final String ACTIVE_BORROW_BY_READER_SQL = """
            SELECT EXISTS (
                SELECT 1
                FROM borrow_slips
                WHERE reader_id = ?
                  AND status IN ('BORROWING', 'OVERDUE')
            )
            """;

    private final JdbcTemplate jdbcTemplate;

    public ActiveBorrowQueryAdapter(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = Objects.requireNonNull(
                jdbcTemplate,
                "JdbcTemplate must not be null."
        );
    }

    @Override
    public boolean hasActiveBorrowByReaderId(Long readerId) {
        if (readerId == null || readerId <= 0) {
            throw new IllegalArgumentException(
                    "Reader id must be greater than 0."
            );
        }

        Boolean result = jdbcTemplate.queryForObject(
                ACTIVE_BORROW_BY_READER_SQL,
                Boolean.class,
                readerId
        );

        return Boolean.TRUE.equals(result);
    }
}