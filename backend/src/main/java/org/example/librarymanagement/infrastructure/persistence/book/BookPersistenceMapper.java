package org.example.librarymanagement.infrastructure.persistence.book;


import org.example.librarymanagement.domain.entity.Book;
import org.springframework.stereotype.Component;


@Component
public class BookPersistenceMapper {



    public BookJpaEntity toEntity(Book book) {


        return new BookJpaEntity(

                book.getId(),

                book.getTitle(),

                book.getAuthor(),

                book.getIsbn(),

                book.getDescription(),

                book.getCoverImageUrl(),

                book.getPublisher(),

                book.getPublishedYear(),

                book.getShelfLocation(),

                book.getTotalQuantity(),

                book.getAvailableQuantity(),

                book.getCategoryId(),

                book.isActive(),

                book.getCreatedAt(),

                book.getUpdatedAt()
        );
    }



    public Book toDomain(BookJpaEntity entity) {


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
}