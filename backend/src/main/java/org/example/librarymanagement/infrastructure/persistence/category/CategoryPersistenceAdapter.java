package org.example.librarymanagement.infrastructure.persistence.category;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.example.librarymanagement.port.outbound.category.LoadCategoryPort;
import org.springframework.stereotype.Component;

@Component
public class CategoryPersistenceAdapter implements LoadCategoryPort {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryPersistenceAdapter(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Map<Long, String> findCategoryNamesByIds(Set<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return categoryJpaRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(CategoryJpaEntity::getId, CategoryJpaEntity::getName));
    }
}
