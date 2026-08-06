package org.example.librarymanagement.infrastructure.persistence.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<BookJpaEntity, Long> {

    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
