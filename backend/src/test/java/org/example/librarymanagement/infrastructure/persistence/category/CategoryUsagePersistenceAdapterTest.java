package org.example.librarymanagement.infrastructure.persistence.category;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

@ExtendWith(MockitoExtension.class)
class CategoryUsagePersistenceAdapterTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void passesLongCategoryIdDirectlyToJdbcTemplate() {
        Long categoryId = 10L;
        CategoryUsagePersistenceAdapter adapter =
                new CategoryUsagePersistenceAdapter(jdbcTemplate);

        when(jdbcTemplate.queryForObject(
                org.mockito.ArgumentMatchers.contains(
                        "WHERE category_id = ?"
                ),
                eq(Boolean.class),
                same(categoryId)
        )).thenReturn(true);

        assertTrue(adapter.isCategoryUsedByAnyBook(categoryId));

        verify(jdbcTemplate).queryForObject(
                org.mockito.ArgumentMatchers.contains(
                        "WHERE category_id = ?"
                ),
                eq(Boolean.class),
                same(categoryId)
        );
    }
}
