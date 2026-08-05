package org.example.librarymanagement.infrastructure.persistence.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.example.librarymanagement.LibraryManagementApplication;
import org.example.librarymanagement.domain.entity.Category;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = LibraryManagementApplication.class)
@Transactional
class CategoryPersistenceIntegrationTest {

    @Autowired
    private CategoryRepositoryPort categoryRepositoryPort;

    @Test
    void savesAndLoadsCategory() {
        Category category = Category.create(
                "  Literature    Fiction  ",
                "  Book    category  "
        );

        Category saved =
                categoryRepositoryPort.save(category);

        assertNotNull(saved.getId());
        assertEquals("Literature Fiction", saved.getName());
        assertEquals(
                "Book category",
                saved.getDescription()
        );

        Optional<Category> found =
                categoryRepositoryPort.findById(
                        saved.getId()
                );

        assertTrue(found.isPresent());
        assertEquals(
                "Literature Fiction",
                found.orElseThrow().getName()
        );
    }

    @Test
    void returnsCategoriesOrderedByName() {
        categoryRepositoryPort.save(
                Category.create("Literature", null)
        );

        categoryRepositoryPort.save(
                Category.create("Science", null)
        );

        var categories =
                categoryRepositoryPort.findAll();

        assertEquals(2, categories.size());
        assertEquals(
                "Literature",
                categories.get(0).getName()
        );
        assertEquals(
                "Science",
                categories.get(1).getName()
        );
    }
}
