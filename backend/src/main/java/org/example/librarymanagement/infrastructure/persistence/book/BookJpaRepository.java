package org.example.librarymanagement.infrastructure.persistence.book;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface BookJpaRepository extends JpaRepository<BookJpaEntity, Long> {

    @Query(value = """
        SELECT CASE WHEN EXISTS (
            SELECT 1 FROM borrow_details bd 
            JOIN borrow_slips bs ON bd.borrow_slip_id = bs.id 
            WHERE bd.book_id = :bookId AND bs.status IN ('BORROWING', 'OVERDUE')
        ) THEN 1 ELSE 0 END
        """, nativeQuery = true)
    int existsActiveBorrowByBookId(@Param("bookId") Long bookId);

    Page<BookJpaEntity> searchBooks(
            String title, String author, String isbn, Pageable pageable
    );

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BookJpaEntity b where b.id = :id")
    Optional<BookJpaEntity> findByIdForUpdate(@Param("id") Long id);
}
