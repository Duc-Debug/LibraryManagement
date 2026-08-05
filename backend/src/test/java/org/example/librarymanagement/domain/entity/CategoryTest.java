package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;

import org.example.librarymanagement.domain.exceptions.DomainException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @Test
    void createHasNullIdAndCanonicalizesText() {
        Category category =
                Category.create(
                        "  Science   Fiction  ",
                        "  Books   about futures  "
                );

        assertNull(category.getId());
        assertEquals("Science Fiction", category.getName());
        assertEquals("Books about futures", category.getDescription());
    }

    @Test
    void restoreRejectsNullId() {
        assertThrows(
                DomainException.class,
                () -> Category.restore(
                        null,
                        "Science",
                        null,
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void restoreRejectsNonPositiveId() {
        assertThrows(
                DomainException.class,
                () -> Category.restore(
                        0L,
                        "Science",
                        null,
                        true,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        );
    }

    @Test
    void rejectsNameLongerThanSchemaLimit() {
        String tooLongName =
                "a".repeat(Category.MAX_NAME_LENGTH + 1);

        assertThrows(
                DomainException.class,
                () -> Category.create(
                        tooLongName,
                        null
                )
        );
    }

    @Test
    void rejectsDescriptionLongerThanSchemaLimit() {
        String tooLongDescription =
                "a".repeat(Category.MAX_DESCRIPTION_LENGTH + 1);

        assertThrows(
                DomainException.class,
                () -> Category.create(
                        "Science",
                        tooLongDescription
                )
        );
    }

    @Test
    void restoreRejectsUpdatedAtBeforeCreatedAt() {
        LocalDateTime createdAt =
                LocalDateTime.now();

        assertThrows(
                DomainException.class,
                () -> Category.restore(
                        1L,
                        "Science",
                        null,
                        true,
                        createdAt,
                        createdAt.minusSeconds(1)
                )
        );
    }

    @Test
    void unsavedCategoriesAreNotEqualUnlessSameInstance() {
        Category first =
                Category.create(
                        "Science",
                        null
                );

        Category second =
                Category.create(
                        "Science",
                        null
                );

        assertEquals(first, first);
        assertNotEquals(first, second);
    }
}
