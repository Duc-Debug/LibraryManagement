package org.example.librarymanagement.domain.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.example.librarymanagement.domain.exceptions.DomainException;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
@Builder
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {

    @EqualsAndHashCode.Include
    private UUID id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String coverImageUrl;
    private String publisher;
    private Integer publishedYear;
    private String shelfLocation;
    private int totalQuantity;
    private int availableQuantity;
    private UUID categoryId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 1. Constructor tạo mới sách
    public Book(String title, String author, String isbn, int totalQuantity, UUID categoryId) {
        this.id = UUID.randomUUID();
        this.title = requireNotBlank(title, "Title cannot be null or empty");
        this.author = requireNotBlank(author, "Author cannot be null or empty");
        this.isbn = requireNotBlank(isbn, "ISBN cannot be null or empty");
        
        if (totalQuantity < 0) {
            throw new DomainException("Total quantity cannot be negative");
        }
        if (categoryId == null) {
            throw new DomainException("Category ID cannot be null");
        }

        this.totalQuantity = totalQuantity;
        this.availableQuantity = totalQuantity;
        this.categoryId = categoryId;
        this.active = true;

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    // 2. Constructor Re-constitute từ Database
    public Book(UUID id, String title, String author, String isbn, String description,
                String coverImageUrl, String publisher, Integer publishedYear,
                String shelfLocation, int totalQuantity, int availableQuantity,
                UUID categoryId, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {

        this.id = id != null ? id : UUID.randomUUID();
        this.title = requireNotBlank(title, "Title cannot be null or empty");
        this.author = requireNotBlank(author, "Author cannot be null or empty");
        this.isbn = requireNotBlank(isbn, "ISBN cannot be null or empty");
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.publisher = publisher;
        this.publishedYear = publishedYear;
        this.shelfLocation = shelfLocation;

        if (totalQuantity < 0) {
            throw new DomainException("Total quantity cannot be negative");
        }
        if (availableQuantity < 0 || availableQuantity > totalQuantity) {
            throw new DomainException("Available quantity must be between 0 and total quantity");
        }
        if (categoryId == null) {
            throw new DomainException("Category ID cannot be null");
        }

        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.categoryId = categoryId;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // --- DOMAIN BUSINESS METHODS ---

    public boolean isAvailableForBorrow() {
        return this.active && this.availableQuantity > 0;
    }

    public void decreaseAvailableQuantity() {
        if (!isAvailableForBorrow()) {
            throw new DomainException("Sách hiện không còn sẵn để mượn.");
        }
        this.availableQuantity--;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseAvailableQuantity() {
        if (this.availableQuantity >= this.totalQuantity) {
            throw new DomainException("Số lượng có sẵn không thể vượt quá tổng số lượng.");
        }
        this.availableQuantity++;
        this.updatedAt = LocalDateTime.now();
    }

    // Phương thức cập nhật thông tin sách nghiệp vụ
    public void updateDetails(String title, String author, String description, 
                              String publisher, Integer publishedYear, String shelfLocation) {
        this.title = requireNotBlank(title, "Title cannot be null or empty");
        this.author = requireNotBlank(author, "Author cannot be null or empty");
        this.description = description;
        this.publisher = publisher;
        this.publishedYear = publishedYear;
        this.shelfLocation = shelfLocation;
        this.updatedAt = LocalDateTime.now();
    }

    private static String requireNotBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new DomainException(errorMessage);
        }
        return value.trim();
    }
}