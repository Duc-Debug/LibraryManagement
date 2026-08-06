package org.example.librarymanagement.infrastructure.persistence.category;

import java.util.Objects;

import org.example.librarymanagement.port.outbound.category.CategoryUsagePort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryUsagePersistenceAdapter
        implements CategoryUsagePort {

    private static final String EXISTS_BOOK_BY_CATEGORY_SQL = """
            SELECT EXISTS (
                SELECT 1
                FROM books
                WHERE category_id = ?
                LIMIT 1
            )
            """;

    private final JdbcTemplate jdbcTemplate;

    public CategoryUsagePersistenceAdapter(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate =
                Objects.requireNonNull(
                        jdbcTemplate,
                        "JdbcTemplate must not be null"
                );
    }

    @Override
    public boolean isCategoryUsedByAnyBook(
            Long categoryId
    ) {
        Objects.requireNonNull(
                categoryId,
                "Category id must not be null"
        );

        Boolean result =
                jdbcTemplate.queryForObject(
                        EXISTS_BOOK_BY_CATEGORY_SQL,
                        Boolean.class,
                        categoryId
                );

        return Boolean.TRUE.equals(result);
    }
}