package org.example.librarymanagement.infrastructure.persistence.borrow;

import org.example.librarymanagement.domain.entity.BorrowSlip;
import org.example.librarymanagement.domain.enums.BorrowSlipStatus;
import org.example.librarymanagement.port.dtos.borrow.BorrowSlipResponseDto;
import org.springframework.stereotype.Component;

@Component
public class BorrowSlipPersistenceMapper {

    /**
     * Chuyển đổi từ Projection (kết quả Query tối ưu từ DB) sang Response DTO cho Tầng Application / Web
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
                projection.getCreatedAt()
        );
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
                entity.getReaderId(),
                entity.getBorrowedAt(),
                entity.getDueAt(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    /**
     * Chuyển đổi từ Domain Entity sang JPA Entity (để lưu vào DB khi tạo/cập nhật)
     */
    public BorrowSlipJpaEntity toJpaEntity(BorrowSlip domain, String borrowCode, Long createdByUserId, String note) {
        if (domain == null) {
            return null;
        }

        return new BorrowSlipJpaEntity(
                domain.getId(),
                borrowCode,
                domain.getReaderId(),
                createdByUserId,
                domain.getBorrowDate(),
                domain.getDueDate(),
                domain.getStatus(),
                note,
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}