package org.example.librarymanagement.infrastructure.persistence.book;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends JpaRepository<BookJpaEntity,Long> {
    
}
