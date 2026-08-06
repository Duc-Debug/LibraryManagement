package org.example.librarymanagement.infrastructure.persistence.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookJpaRepository extends JpaRepository<BookJpaEntity, Long> {

    @Query(value = "SELECT COUNT(*) FROM borrow_details bd " +
                   "JOIN borrow_slips bs ON bd.borrow_slip_id = bs.id " +
                   "WHERE bd.book_id = :bookId AND bs.status IN ('BORROWING', 'OVERDUE')", nativeQuery = true)
    long countActiveBorrowByBookId(@Param("bookId") Long bookId);

    Page<BookJpaEntity> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(
            String title, String author, Pageable pageable
    );
}
