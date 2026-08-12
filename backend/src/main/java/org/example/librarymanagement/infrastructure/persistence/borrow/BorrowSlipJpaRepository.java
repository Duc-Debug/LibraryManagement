package org.example.librarymanagement.infrastructure.persistence.borrow;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowSlipJpaRepository extends JpaRepository<BorrowSlipJpaEntity, Long> {

    @Query(value = """
        SELECT 
            bs.id AS id,
            bs.borrow_code AS borrowCode,
            bs.reader_id AS readerId,
            r.card_code AS readerCardNumber,
            r.full_name AS readerName,
            bs.created_by_user_id AS createdByUserId,
            u.full_name AS createdByUserName,
            bs.borrowed_at AS borrowedAt,
            bs.due_at AS dueAt,
            bs.status AS status,
            bs.note AS note,
            (SELECT COUNT(1) FROM borrow_details bd WHERE bd.borrow_slip_id = bs.id) AS totalBooks,
            bs.created_at AS createdAt
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        LEFT JOIN users u ON bs.created_by_user_id = u.id
        WHERE (:status IS NULL OR bs.status = :status)
        AND (
            :keyword IS NULL 
            OR bs.borrow_code LIKE CONCAT('%', :keyword, '%')
            OR r.full_name LIKE CONCAT('%', :keyword, '%')
            OR r.card_code LIKE CONCAT('%', :keyword, '%')
        )
        ORDER BY bs.borrowed_at DESC, bs.id DESC
        """,
        countQuery = """
        SELECT COUNT(bs.id)
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        WHERE (:status IS NULL OR bs.status = :status)
        AND (
            :keyword IS NULL 
            OR bs.borrow_code LIKE CONCAT('%', :keyword, '%')
            OR r.full_name LIKE CONCAT('%', :keyword, '%')
            OR r.card_code LIKE CONCAT('%', :keyword, '%')
        )
        """,
        nativeQuery = true)
    Page<BorrowSlipSummaryProjection> findBorrowSlipsWithFilter(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(value = """
        SELECT 
            bs.id AS id,
            bs.borrow_code AS borrowCode,
            bs.reader_id AS readerId,
            r.card_code AS readerCardNumber,
            r.full_name AS readerName,
            bs.created_by_user_id AS createdByUserId,
            u.full_name AS createdByUserName,
            bs.borrowed_at AS borrowedAt,
            bs.due_at AS dueAt,
            bs.status AS status,
            bs.note AS note,
            (SELECT COUNT(1) FROM borrow_details bd WHERE bd.borrow_slip_id = bs.id) AS totalBooks,
            bs.created_at AS createdAt
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        LEFT JOIN users u ON bs.created_by_user_id = u.id
        WHERE bs.id = :id
        """,
        nativeQuery = true)
    Optional<BorrowSlipSummaryProjection> findBorrowSlipDetailById(@Param("id") Long id);

    boolean existsByBorrowCode(String borrowCode);
}