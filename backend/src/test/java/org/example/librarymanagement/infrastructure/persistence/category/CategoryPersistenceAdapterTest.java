package org.example.librarymanagement.infrastructure.persistence.category;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.example.librarymanagement.application.category.exception.CategoryInUseException;
import org.example.librarymanagement.application.category.exception.DuplicateCategoryNameException;
import org.example.librarymanagement.domain.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class CategoryPersistenceAdapterTest {

    private final CategoryJpaRepository categoryJpaRepository =
            mock(CategoryJpaRepository.class);

    private final CategoryPersistenceAdapter adapter =
            new CategoryPersistenceAdapter(
                    categoryJpaRepository,
                    new CategoryPersistenceMapper()
            );

    @Test
    void mapsUniqueNameConstraintToDuplicateCategoryNameException() {
        Category category =
                Category.create(
                        "Science",
                        null
                );

        when(categoryJpaRepository.saveAndFlush(
                any(CategoryJpaEntity.class)
        )).thenThrow(
                new DataIntegrityViolationException(
                        "Duplicate entry for uk_categories_name"
                )
        );

        assertThrows(
                DuplicateCategoryNameException.class,
                () -> adapter.save(category)
        );
    }

    @Test
    void rethrowsUnknownSaveDataIntegrityViolation() {
        Category category =
                Category.create(
                        "Science",
                        null
                );

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Some other constraint"
                );

        when(categoryJpaRepository.saveAndFlush(
                any(CategoryJpaEntity.class)
        )).thenThrow(exception);

        DataIntegrityViolationException thrown =
                assertThrows(
                        DataIntegrityViolationException.class,
                        () -> adapter.save(category)
                );

        assertSame(exception, thrown);
    }

    @Test
    void mapsBookForeignKeyConstraintToCategoryInUseException() {
        Category category =
                persistedCategory();

        doThrow(
                new DataIntegrityViolationException(
                        "Cannot delete due to fk_books_category"
                )
        ).when(categoryJpaRepository).flush();

        assertThrows(
                CategoryInUseException.class,
                () -> adapter.delete(category)
        );
    }

    private Category persistedCategory() {
        LocalDateTime createdAt =
                LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt =
                createdAt.plusHours(1);

        return Category.restore(
                1L,
                "Science",
                null,
                true,
                createdAt,
                updatedAt
        );
    }
}
