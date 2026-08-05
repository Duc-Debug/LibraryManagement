package org.example.librarymanagement.infrastructure.persistence.book;



import org.springframework.data.jpa.repository.JpaRepository;


public interface BookJpaRepository 
        extends JpaRepository<BookJpaEntity, Long> {


    boolean existsByIsbnIgnoreCase(String isbn);

}