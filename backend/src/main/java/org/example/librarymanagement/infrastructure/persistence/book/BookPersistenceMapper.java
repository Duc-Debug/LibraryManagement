package org.example.librarymanagement.infrastructure.persistence.book;

import org.example.librarymanagement.domain.entity.Book;

public class BookPersistenceMapper {
    public static Book toDomain(BookJpaEntity entity){
        if(entity==null) return null;
        return new Book(
             entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getIsbn(),
                entity.getDescription(),
                entity.getCoverImageUrl(),
                entity.getPublisher(),
                entity.getPublishedYear(),
                entity.getShelfLocation(),
                entity.getTotalQuantity(),
                entity.getAvailableQuantity(),
                entity.getCategoryId(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
    public static BookJpaEntity toJpaEntity(Book domain) {
        if (domain == null) return null;
        return new BookJpaEntity(
                domain.getBookId(),
                domain.getTitle(),
                domain.getAuthor(),
                domain.getIsbn(),
                domain.getDescription(),
                domain.getCoverImageUrl(),
                domain.getPublisher(),
                domain.getPublishedYear(),
                domain.getShelfLocation(),
                domain.getTotalQuantity(),
                domain.getAvailableQuantity(),
                domain.getCategoryId(),
                domain.isActive(),
                domain.getCreatedAt(),
                domain.getUpdatedAt()
        );
    }
}
