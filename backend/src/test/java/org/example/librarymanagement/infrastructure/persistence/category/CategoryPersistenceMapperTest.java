package org.example.librarymanagement.infrastructure.persistence.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.entity.Category;
import org.junit.jupiter.api.Test;

class CategoryPersistenceMapperTest {

    private final CategoryPersistenceMapper mapper =
            new CategoryPersistenceMapper();

    @Test
    void mapsDomainToJpaEntity() {
        LocalDateTime createdAt =
                LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt =
                createdAt.plusHours(1);

        Category category =
                Category.restore(
                        1L,
                        "Science",
                        "Books",
                        true,
                        createdAt,
                        updatedAt
                );

        CategoryJpaEntity entity =
                mapper.toJpaEntity(category);

        assertEquals(1L, entity.getId());
        assertEquals("Science", entity.getName());
        assertEquals("Books", entity.getDescription());
        assertTrue(entity.isActive());
        assertEquals(createdAt, entity.getCreatedAt());
        assertEquals(updatedAt, entity.getUpdatedAt());
    }

    @Test
    void mapsNewDomainCategoryToJpaEntityWithNullId() {
        Category category =
                Category.create(
                        "Science",
                        null
                );

        CategoryJpaEntity entity =
                mapper.toJpaEntity(category);

        assertNull(entity.getId());
        assertEquals("Science", entity.getName());
    }

    @Test
    void mapsJpaEntityToDomain() {
        LocalDateTime createdAt =
                LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt =
                createdAt.plusHours(1);

        CategoryJpaEntity entity =
                new CategoryJpaEntity(
                        1L,
                        "Science",
                        "Books",
                        true,
                        createdAt,
                        updatedAt
                );

        Category category =
                mapper.toDomain(entity);

        assertEquals(1L, category.getId());
        assertEquals("Science", category.getName());
        assertEquals("Books", category.getDescription());
        assertTrue(category.isActive());
        assertEquals(createdAt, category.getCreatedAt());
        assertEquals(updatedAt, category.getUpdatedAt());
    }
}
