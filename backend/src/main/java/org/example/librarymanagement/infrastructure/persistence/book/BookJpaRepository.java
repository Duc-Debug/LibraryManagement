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

    Page<BookJpaEntity> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String title, String author, String isbn, Pageable pageable);

    boolean existsByIsbnIgnoreCase(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BookJpaEntity b where b.id = :id")
    Optional<BookJpaEntity> findByIdForUpdate(@Param("id") Long id);

    @Query("""
    SELECT b FROM BookJpaEntity b 
    WHERE (:keyword IS NULL OR :keyword = '' 
           OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) 
           OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) 
           OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%')))
      AND (:categoryId IS NULL OR b.categoryId = :categoryId)
      AND (:availability IS NULL OR :availability = '' OR :availability = 'ALL' 
           OR (:availability = 'AVAILABLE' AND b.availableQuantity > 0)
           OR (:availability = 'OUT_OF_STOCK' AND b.availableQuantity = 0))
""")
    Page<BookJpaEntity> filterBooks(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("availability") String availability,
            Pageable pageable
    );
}
