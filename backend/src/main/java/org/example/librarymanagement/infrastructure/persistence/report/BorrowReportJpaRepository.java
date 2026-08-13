package org.example.librarymanagement.infrastructure.persistence.report;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowReportJpaRepository extends JpaRepository<org.example.librarymanagement.infrastructure.persistence.borrow.BorrowSlipJpaEntity, Long> {

    @Query(value = """
        SELECT 
            bs.id AS borrowSlipId,
            bs.borrow_code AS borrowCode,
            r.card_code AS readerCardCode,
            r.full_name AS readerName,
            GROUP_CONCAT(DISTINCT b.title SEPARATOR ', ') AS bookTitles,
            (SELECT CAST(COUNT(1) AS SIGNED) FROM borrow_details bd2 WHERE bd2.borrow_slip_id = bs.id) AS totalBooks,
            bs.borrowed_at AS borrowedAt,
            bs.due_at AS dueAt,
            MAX(bd.returned_at) AS actualReturnedAt,
            bs.status AS status,
            SUM(COALESCE(bd.fine_amount, 0)) AS fineAmount,
            u.full_name AS createdByUserName
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        LEFT JOIN users u ON bs.created_by_user_id = u.id
        LEFT JOIN borrow_details bd ON bs.id = bd.borrow_slip_id
        LEFT JOIN books b ON bd.book_id = b.id
        WHERE bs.borrowed_at >= :startDateTime
          AND bs.borrowed_at <= :endDateTime
          AND (:status IS NULL OR bs.status = :status)
          AND (
            :keyword IS NULL 
            OR bs.borrow_code LIKE CONCAT('%', :keyword, '%')
            OR r.full_name LIKE CONCAT('%', :keyword, '%')
            OR r.card_code LIKE CONCAT('%', :keyword, '%')
          )
        GROUP BY bs.id, bs.borrow_code, r.card_code, r.full_name, bs.borrowed_at, bs.due_at, bs.status, u.full_name
        ORDER BY bs.borrowed_at DESC, bs.id DESC
        """,
        countQuery = """
        SELECT COUNT(DISTINCT bs.id)
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        WHERE bs.borrowed_at >= :startDateTime
          AND bs.borrowed_at <= :endDateTime
          AND (:status IS NULL OR bs.status = :status)
          AND (
            :keyword IS NULL 
            OR bs.borrow_code LIKE CONCAT('%', :keyword, '%')
            OR r.full_name LIKE CONCAT('%', :keyword, '%')
            OR r.card_code LIKE CONCAT('%', :keyword, '%')
          )
        """,
        nativeQuery = true)
    Page<BorrowReportProjection> findReportProjectionsWithFilter(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query(value = """
        SELECT 
            bs.id AS borrowSlipId,
            bs.borrow_code AS borrowCode,
            r.card_code AS readerCardCode,
            r.full_name AS readerName,
            GROUP_CONCAT(DISTINCT b.title SEPARATOR ', ') AS bookTitles,
            (SELECT CAST(COUNT(1) AS SIGNED) FROM borrow_details bd2 WHERE bd2.borrow_slip_id = bs.id) AS totalBooks,
            bs.borrowed_at AS borrowedAt,
            bs.due_at AS dueAt,
            MAX(bd.returned_at) AS actualReturnedAt,
            bs.status AS status,
            SUM(COALESCE(bd.fine_amount, 0)) AS fineAmount,
            u.full_name AS createdByUserName
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        LEFT JOIN users u ON bs.created_by_user_id = u.id
        LEFT JOIN borrow_details bd ON bs.id = bd.borrow_slip_id
        LEFT JOIN books b ON bd.book_id = b.id
        WHERE bs.borrowed_at >= :startDateTime
          AND bs.borrowed_at <= :endDateTime
          AND (:status IS NULL OR bs.status = :status)
          AND (
            :keyword IS NULL 
            OR bs.borrow_code LIKE CONCAT('%', :keyword, '%')
            OR r.full_name LIKE CONCAT('%', :keyword, '%')
            OR r.card_code LIKE CONCAT('%', :keyword, '%')
          )
        GROUP BY bs.id, bs.borrow_code, r.card_code, r.full_name, bs.borrowed_at, bs.due_at, bs.status, u.full_name
        ORDER BY bs.borrowed_at DESC, bs.id DESC
        """,
        nativeQuery = true)
    List<BorrowReportProjection> findAllReportProjectionsWithFilter(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") String status,
            @Param("keyword") String keyword
    );

    @Query(value = """
        SELECT 
            COUNT(DISTINCT bs.id) AS totalSlips,
            CAST(COALESCE(SUM(sub.book_count), 0) AS SIGNED) AS totalBooksBorrowed,
            CAST(COALESCE(SUM(CASE WHEN bs.status = 'RETURNED' THEN 1 ELSE 0 END), 0) AS SIGNED) AS totalReturned,
            CAST(COALESCE(SUM(CASE WHEN bs.status = 'OVERDUE' THEN 1 ELSE 0 END), 0) AS SIGNED) AS totalOverdue,
            CAST(COALESCE(SUM(CASE WHEN bs.status = 'BORROWING' THEN 1 ELSE 0 END), 0) AS SIGNED) AS totalBorrowing,
            COALESCE(SUM(bd.fine_amount), 0) AS totalFineAmount
        FROM borrow_slips bs
        LEFT JOIN readers r ON bs.reader_id = r.id
        LEFT JOIN borrow_details bd ON bs.id = bd.borrow_slip_id
        LEFT JOIN (
            SELECT borrow_slip_id, COUNT(1) AS book_count
            FROM borrow_details
            GROUP BY borrow_slip_id
        ) sub ON bs.id = sub.borrow_slip_id
        WHERE bs.borrowed_at >= :startDateTime
          AND bs.borrowed_at <= :endDateTime
          AND (:status IS NULL OR bs.status = :status)
          AND (
            :keyword IS NULL 
            OR bs.borrow_code LIKE CONCAT('%', :keyword, '%')
            OR r.full_name LIKE CONCAT('%', :keyword, '%')
            OR r.card_code LIKE CONCAT('%', :keyword, '%')
          )
        """,
        nativeQuery = true)
    BorrowReportSummaryAggregationProjection calculateSummaryNative(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("status") String status,
            @Param("keyword") String keyword
    );
}
