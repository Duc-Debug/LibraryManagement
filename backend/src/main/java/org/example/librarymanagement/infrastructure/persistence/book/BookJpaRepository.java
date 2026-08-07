package org.example.librarymanagement.infrastructure.persistence.book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends JpaRepository<BookJpaEntity, Long> {

    boolean existsByIsbnIgnoreCase(String isbn);

    @Query(value = """
       SELECT CASE WHEN EXISTS (
           SELECT 1 FROM borrow_details bd 
           JOIN borrow_slips bs ON bd.borrow_slip_id = bs.id 
           WHERE bd.book_id = :bookId AND bs.status IN ('BORROWING', 'OVERDUE')
       ) THEN 1 ELSE 0 END
       """, nativeQuery = true)
    int existsActiveBorrowByBookId(@Param("bookId") Long bookId);

    Page<BookJpaEntity> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(
            String title, String author, String isbn, Pageable pageable
    );
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndIdNot(String isbn, Long id);
}
