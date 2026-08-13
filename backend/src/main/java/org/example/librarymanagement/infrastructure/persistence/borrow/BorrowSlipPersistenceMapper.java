package org.example.librarymanagement.infrastructure.persistence.borrow;

import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.springframework.stereotype.Component;

@Component
public class BorrowSlipPersistenceMapper {

    /**
     * Chuyển đổi từ Projection (kết quả Query tối ưu từ DB) sang Response DTO cho
     * Tầng Application / Web
     */
    public BorrowSlipResponseDto toDto(BorrowSlipSummaryProjection projection) {
        if (projection == null) {
            return null;
        }

        BorrowSlipStatus status = null;
        if (projection.getStatus() != null) {
            try {
                status = BorrowSlipStatus.valueOf(projection.getStatus());
            } catch (IllegalArgumentException ignored) {
            }
        }

        Integer totalBooks = projection.getTotalBooks();
        int safeTotalBooks = (totalBooks != null) ? totalBooks.intValue() : 0;

        return new BorrowSlipResponseDto(
                projection.getId(),
                projection.getBorrowCode(),
                projection.getReaderId(),
                projection.getReaderCardNumber(),
                projection.getReaderName(),
                projection.getCreatedByUserId(),
                projection.getCreatedByUserName(),
                projection.getBorrowedAt(),
                projection.getDueAt(),
                status,
                projection.getNote(),
                safeTotalBooks,
                projection.getCreatedAt());
    }

    /**
     * Chuyển đổi từ JPA Entity sang Domain Entity (Pure Java, round-trip hoàn hảo)
     */
    public BorrowSlip toDomain(BorrowSlipJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new BorrowSlip(
                entity.getId(),
                entity.getBorrowCode(),
                entity.getReaderId(),
                entity.getCreatedByUserId(),
                entity.getBorrowedAt(),
                entity.getDueAt(),
                entity.getReturnDate(),
                entity.getStatus(),
                entity.getNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    /**
     * Chuyển đổi từ Domain Entity sang JPA Entity (để lưu vào DB khi tạo/cập nhật)
     */
    public BorrowSlipJpaEntity toJpaEntity(BorrowSlip domain) {
        if (domain == null) {
            return null;
        }

        return new BorrowSlipJpaEntity(
                domain.getId(),
                domain.getBorrowCode(),
                domain.getReaderId(),
                domain.getCreatedByUserId(),
                domain.getBorrowDate(),
                domain.getDueDate(),
                domain.getReturnDate(),
                domain.getStatus(),
                domain.getNote(),
                domain.getCreatedAt(),
                domain.getUpdatedAt());
    }

    public void updateJpaEntity(
            BorrowSlip domain,
            BorrowSlipJpaEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setDueAt(domain.getDueDate());
        entity.setReturnDate(domain.getReturnDate());
        entity.setStatus(domain.getStatus());
        entity.setNote(domain.getNote());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}