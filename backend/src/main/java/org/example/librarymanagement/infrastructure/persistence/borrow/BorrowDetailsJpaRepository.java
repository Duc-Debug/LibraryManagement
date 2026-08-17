package org.example.librarymanagement.infrastructure.persistence.borrow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;

@Repository
public interface BorrowDetailsJpaRepository
        extends JpaRepository<BorrowDetailsJpaEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT bd
            FROM BorrowDetailsJpaEntity bd
            WHERE bd.borrowSlipId = :borrowSlipId
            ORDER BY bd.id
            """)
    List<BorrowDetailsJpaEntity> findByBorrowSlipIdForUpdate(
            @Param("borrowSlipId") Long borrowSlipId
    );

    @Query(value = """
            SELECT 
                bd.id AS id,
                bd.book_id AS bookId,
                b.title AS bookTitle,
                b.isbn AS isbn,
                b.author AS author,
                c.name AS categoryName,
                b.cover_image_url AS coverUrl,
                bd.returned_at AS returnedAt,
                bd.fine_amount AS fineAmount,
                bd.fine_reason AS fineReason
            FROM borrow_details bd
            LEFT JOIN books b ON bd.book_id = b.id
            LEFT JOIN categories c ON b.category_id = c.id
            WHERE bd.borrow_slip_id = :borrowSlipId
            ORDER BY bd.id ASC
            """, nativeQuery = true)
    List<BorrowDetailItemProjection> findItemsByBorrowSlipId(@Param("borrowSlipId") Long borrowSlipId);
}