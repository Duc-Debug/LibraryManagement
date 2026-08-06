package org.example.librarymanagement.infrastructure.persistence.category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository
        extends JpaRepository<CategoryJpaEntity, Long> {

    boolean existsByName(String name);

    Optional<CategoryJpaEntity> findByName(String name);

    boolean existsByNameAndIdNot(
            String name,
            Long excludedCategoryId
    );

    List<CategoryJpaEntity> findAllByOrderByNameAsc();
}
