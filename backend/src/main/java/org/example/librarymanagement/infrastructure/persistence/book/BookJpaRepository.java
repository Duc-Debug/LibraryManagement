package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


public interface BookJpaRepository 
        extends JpaRepository<BookJpaEntity, UUID> {


    boolean existsByIsbnIgnoreCase(String isbn);

}