package org.example.librarymanagement.infrastructure.persistence.file;

import java.util.List;

import org.example.librarymanagement.domain.entity.FileCleanupStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileCleanupTaskJpaRepository extends JpaRepository<FileCleanupTaskJpaEntity, Long> {

    List<FileCleanupTaskJpaEntity> findByStatusOrderByCreatedAtAsc(
            FileCleanupStatus status,
            Pageable pageable
    );
}
