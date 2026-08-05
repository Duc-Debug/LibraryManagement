package org.example.librarymanagement.infrastructure.persistence.category;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.example.librarymanagement.application.category.exception.CategoryInUseException;
import org.example.librarymanagement.application.category.exception.DuplicateCategoryNameException;
import org.example.librarymanagement.domain.entity.Category;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryPersistenceAdapter
        implements CategoryRepositoryPort {

    private static final String UNIQUE_NAME_CONSTRAINT =
            "uk_categories_name";

    private static final String BOOK_CATEGORY_FOREIGN_KEY =
            "fk_books_category";

    private final CategoryJpaRepository categoryJpaRepository;
    private final CategoryPersistenceMapper mapper;

    public CategoryPersistenceAdapter(
            CategoryJpaRepository categoryJpaRepository,
            CategoryPersistenceMapper mapper
    ) {
        this.categoryJpaRepository =
                Objects.requireNonNull(
                        categoryJpaRepository,
                        "Category JPA repository must not be null"
                );

        this.mapper =
                Objects.requireNonNull(
                        mapper,
                        "Category persistence mapper must not be null"
                );
    }

    @Override
    public Category save(
            Category category
    ) {
        Objects.requireNonNull(
                category,
                "Category must not be null"
        );

        try {
            CategoryJpaEntity entity =
                    mapper.toJpaEntity(category);

            CategoryJpaEntity savedEntity =
                    categoryJpaRepository.saveAndFlush(entity);

            return mapper.toDomain(savedEntity);
        } catch (DataIntegrityViolationException exception) {
            if (containsConstraint(
                    exception,
                    UNIQUE_NAME_CONSTRAINT
            )) {
                throw new DuplicateCategoryNameException(
                        category.getName()
                );
            }

            throw exception;
        }
    }

    @Override
    public Optional<Category> findById(
            Long categoryId
    ) {
        Objects.requireNonNull(
                categoryId,
                "Category id must not be null"
        );

        return categoryJpaRepository.findById(categoryId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAllByOrderByNameAsc()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
     @Override
    public boolean existsByName(
            String name
    ) {
        Objects.requireNonNull(
                name,
                "Category name must not be null"
        );

        return categoryJpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(
            String name,
            Long excludedCategoryId
    ) {
        Objects.requireNonNull(
                name,
                "Category name must not be null"
        );

        Objects.requireNonNull(
                excludedCategoryId,
                "Excluded category id must not be null"
        );

        return categoryJpaRepository.existsByNameAndIdNot(
                name,
                excludedCategoryId
        );
    }

    @Override
    public void delete(
            Category category
    ) {
        Objects.requireNonNull(
                category,
                "Category must not be null"
        );

        try {
            categoryJpaRepository.deleteById(category.getId());
            categoryJpaRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            if (containsConstraint(
                    exception,
                    BOOK_CATEGORY_FOREIGN_KEY
            )) {
                throw new CategoryInUseException(
                        category.getId()
                );
            }

            throw exception;
        }
    }

    private static boolean containsConstraint(
            Throwable throwable,
            String constraintName
    ) {
        String expectedConstraint =
                constraintName.toLowerCase(Locale.ROOT);

        Throwable current = throwable;

        while (current != null) {
            String message = current.getMessage();

            if (message != null
                    && message.toLowerCase(Locale.ROOT)
                    .contains(expectedConstraint)) {
                return true;
            }

            current = current.getCause();
        }

        return false;
    }
}