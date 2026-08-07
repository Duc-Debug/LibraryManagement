package org.example.librarymanagement.infrastructure.persistence.book;

import org.example.librarymanagement.domain.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookPersistenceMapper {

    public Book toDomain(BookJpaEntity entity) {
        if (entity == null) {
            return null;
        }

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

    public BookJpaEntity toJpaEntity(Book domain) {
        if (domain == null) {
            return null;
        }
        BookJpaEntity entity = new BookJpaEntity();
        updateJpaEntity(domain, entity);
        entity.setId(domain.getBookId());
        return entity;
    }

    public void updateJpaEntity(Book domain, BookJpaEntity entity) {
        if (domain == null || entity == null) {
            return;
        }
        entity.setTitle(domain.getTitle());
        entity.setAuthor(domain.getAuthor());
        entity.setIsbn(domain.getIsbn());
        entity.setDescription(domain.getDescription());
        entity.setCoverImageUrl(domain.getCoverImageUrl());
        entity.setPublisher(domain.getPublisher());
        entity.setPublishedYear(domain.getPublishedYear());
        entity.setShelfLocation(domain.getShelfLocation());
        entity.setTotalQuantity(domain.getTotalQuantity());
        entity.setAvailableQuantity(domain.getAvailableQuantity());
        entity.setCategoryId(domain.getCategoryId());
        entity.setActive(domain.isActive());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }
}