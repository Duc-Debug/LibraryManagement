package org.example.librarymanagement.infrastructure.persistence.category;

import java.util.Objects;

import org.example.librarymanagement.domain.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceMapper {

    public CategoryJpaEntity toJpaEntity(
            Category category
    ) {
        Objects.requireNonNull(
                category,
                "Category must not be null"
        );

        return new CategoryJpaEntity(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }

    public Category toDomain(
            CategoryJpaEntity entity
    ) {
        Objects.requireNonNull(
                entity,
                "Category JPA entity must not be null"
        );

        return Category.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}