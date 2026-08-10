package org.example.librarymanagement.port.outbound.category;

import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.application.category.exceptions.CategoryInUseException;
import org.example.librarymanagement.application.category.exceptions.DuplicateCategoryNameException;
import org.example.librarymanagement.domain.entity.Category;

public interface CategoryRepositoryPort {

    /**
     * Implementations must keep the database unique key uk_categories_name as
     * the final duplicate-name guard.
     *
     * @throws DuplicateCategoryNameException when uk_categories_name is violated
     */
    Category save(Category category);

    Optional<Category> findById(
            Long categoryId
    );

    List<Category> findAll();

    boolean existsByName(
            String name
    );

    boolean existsByNameAndIdNot(
            String name,
            Long excludedCategoryId
    );

    /**
     * Implementations must translate fk_books_category violations.
     *
     * @throws CategoryInUseException when fk_books_category prevents deletion
     */
    void delete(Category category);
}