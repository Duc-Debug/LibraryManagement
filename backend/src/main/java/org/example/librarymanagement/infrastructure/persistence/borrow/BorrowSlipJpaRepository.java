package org.example.librarymanagement.infrastructure.persistence.borrow;

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
            CAST(COUNT(bd.id) AS SIGNED) AS totalBooks,
            bs.created_at AS createdAt
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        LEFT JOIN users u ON bs.created_by_user_id = u.id
        LEFT JOIN borrow_details bd ON bs.id = bd.borrow_slip_id
        WHERE (:status IS NULL OR bs.status = :status)
          AND (
            :keyword IS NULL 
            OR LOWER(bs.borrow_code) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.card_code) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        GROUP BY bs.id, bs.borrow_code, bs.reader_id, r.card_code, r.full_name, 
                 bs.created_by_user_id, u.full_name, bs.borrowed_at, bs.due_at, 
                 bs.status, bs.note, bs.created_at
        """,
        countQuery = """
        SELECT COUNT(DISTINCT bs.id)
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        WHERE (:status IS NULL OR bs.status = :status)
          AND (
            :keyword IS NULL 
            OR LOWER(bs.borrow_code) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.full_name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(r.card_code) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        """,
        nativeQuery = true)
    Page<BorrowSlipSummaryProjection> findBorrowSlipsWithFilter(
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}