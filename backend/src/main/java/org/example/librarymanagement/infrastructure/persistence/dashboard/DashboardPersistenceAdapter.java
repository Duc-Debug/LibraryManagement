package org.example.librarymanagement.infrastructure.persistence.dashboard;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.port.dtos.dashboard.CategoryStatDto;
import org.example.librarymanagement.port.dtos.dashboard.DashboardStatisticsDto;
import org.example.librarymanagement.port.outbound.dashboard.LoadDashboardStatisticsPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardPersistenceAdapter implements LoadDashboardStatisticsPort {

    private static final String GET_DASHBOARD_STATS_SQL = """
        SELECT
            (SELECT COUNT(1) FROM books) AS total_titles,
            (SELECT COALESCE(SUM(total_quantity), 0) FROM books) AS total_copies,
            (SELECT COALESCE(SUM(available_quantity), 0) FROM books) AS available_copies,
            (SELECT COUNT(1) FROM readers) AS total_readers,
            (SELECT COUNT(1) FROM readers WHERE (card_status = 'ACTIVE' OR card_status IS NULL) AND active = 1) AS active_readers,
            (SELECT COUNT(1) FROM categories) AS total_categories,
            (SELECT COUNT(1) FROM borrow_slips WHERE status = 'OVERDUE' OR (status = 'BORROWING' AND DATE(due_at) < DATE(NOW()))) AS overdue_slips
        """;

    private final JdbcTemplate jdbcTemplate;

    public DashboardPersistenceAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "JdbcTemplate must not be null");
    }

    @Override
    public DashboardStatisticsDto loadStatistics() {
        List<CategoryStatDto> categoryDistribution = jdbcTemplate.query(
            """
            SELECT c.name AS category_name, COUNT(b.id) AS book_count
            FROM categories c
            LEFT JOIN books b ON b.category_id = c.id
            GROUP BY c.id, c.name
            ORDER BY book_count DESC, c.name ASC
            """,
            (rs, rowNum) -> new CategoryStatDto(
                rs.getString("category_name"),
                rs.getLong("book_count")
            )
        );

        return jdbcTemplate.queryForObject(GET_DASHBOARD_STATS_SQL, (rs, rowNum) -> {
            long totalTitles = rs.getLong("total_titles");
            long totalCopies = rs.getLong("total_copies");
            long availableCopies = rs.getLong("available_copies");
            long borrowingCopies = Math.max(0, totalCopies - availableCopies);
            long totalReaders = rs.getLong("total_readers");
            long activeReaders = rs.getLong("active_readers");
            long totalCategories = rs.getLong("total_categories");
            long overdueBorrowSlips = rs.getLong("overdue_slips");

            return new DashboardStatisticsDto(
                    totalTitles,
                    totalCopies,
                    availableCopies,
                    borrowingCopies,
                    totalReaders,
                    activeReaders,
                    totalCategories,
                    overdueBorrowSlips,
                    categoryDistribution
            );
        });
    }
}
