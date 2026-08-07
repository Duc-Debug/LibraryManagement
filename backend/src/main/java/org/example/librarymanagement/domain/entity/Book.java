package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;

public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private String coverImageUrl;
    private String publisher;
    private Short publishedYear;
    private String shelfLocation;
    private int totalQuantity;
    private int availableQuantity;
    private Long categoryId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 1. Static Factory Method dùng khi Tạo Mới Sách
    public static Book create(
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            String publisher,
            Short publishedYear,
            String shelfLocation,
            int totalQuantity,
            Long categoryId) {
        LocalDateTime now = LocalDateTime.now();
        return new Book(
                null,
                title,
                author,
                isbn,
                description,
                coverImageUrl,
                publisher,
                publishedYear,
                shelfLocation,
                totalQuantity,
                totalQuantity,
                categoryId,
                true,
                now,
                now);
    }

    // 2. Full-Args Constructor dùng khi Reconstitute Entity từ Database
    public Book(Long id, String title, String author, String isbn, String description,
            String coverImageUrl, String publisher, Short publishedYear,
            String shelfLocation, int totalQuantity, int availableQuantity,
            Long categoryId, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {

        validateBasicInfo(title, author, categoryId);
        validateIsbn(isbn);
        validatePublishedYear(publishedYear);
        validateQuantities(totalQuantity, availableQuantity);

        this.id = id;
        this.title = title.trim();
        this.author = author != null ? author.trim() : null;
        this.isbn = normalizeNullable(isbn);
        this.description = normalizeNullable(description);
        this.coverImageUrl = normalizeNullable(coverImageUrl);
        this.publisher = normalizeNullable(publisher);
        this.publishedYear = publishedYear;
        this.shelfLocation = normalizeNullable(shelfLocation);
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.categoryId = categoryId;
        this.active = active;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    // ==================== DOMAIN BEHAVIORS & INVARIANTS ====================
    public void updateDetails(
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            String publisher,
            Short publishedYear,
            String shelfLocation,
            int newTotalQuantity,
            Long categoryId) {
        validateBasicInfo(title, author, categoryId);
        validateIsbn(isbn);
        validatePublishedYear(publishedYear);

        int borrowedQuantity = this.totalQuantity - this.availableQuantity;
        if (newTotalQuantity < borrowedQuantity) {
            throw new InvalidBookDataException(
                    "New total quantity (" + newTotalQuantity
                            + ") cannot be less than currently borrowed books (" + borrowedQuantity + ").");
        }

        this.title = title.trim();
        this.author = author != null ? author.trim() : null;
        this.isbn = normalizeNullable(isbn);
        this.description = normalizeNullable(description);
        this.coverImageUrl = normalizeNullable(coverImageUrl);
        this.publisher = publisher != null ? publisher.trim() : null;
        this.publishedYear = publishedYear;
        this.shelfLocation = normalizeNullable(shelfLocation);
        this.totalQuantity = newTotalQuantity;
        this.availableQuantity = newTotalQuantity - borrowedQuantity;
        this.categoryId = categoryId;
        touch();
    }

    public void restock(int addedQuantity) {
        if (addedQuantity <= 0) {
            throw new InvalidBookDataException("Added quantity must be greater than 0");
        }
        this.totalQuantity += addedQuantity;
        this.availableQuantity += addedQuantity;
        touch();
    }

    public boolean isAvailableForBorrow() {
        return this.active && this.availableQuantity > 0;
    }

    public void decreaseAvailableQuantity() {
        if (!isAvailableForBorrow()) {
            throw new InvalidBookDataException("Book is currently not available for borrowing");
        }
        this.availableQuantity--;
        touch();
    }

    public void increaseAvailableQuantity() {
        if (this.availableQuantity >= this.totalQuantity) {
            throw new InvalidBookDataException("Available quantity cannot exceed total quantity");
        }
        this.availableQuantity++;
        touch();
    }

    public void deactivate() {
        if (this.active) {
            this.active = false;
            touch();
        }
    }

    public void activate() {
        if (!this.active) {
            this.active = true;
            touch();
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===================== HELPER VALIDATIONS ==================================
    private static void validateNotBlank(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidBookDataException(errorMessage);
        }
    }

    private static void validateBasicInfo(String title, String author, Long categoryId) {
        validateNotBlank(title, "Book title must not be blank");
        validateNotBlank(author, "Author name must not be blank");
        if (categoryId == null) {
            throw new InvalidBookDataException("Category ID must not be null");
        }
    }

    private static void validateIsbn(String isbn) {
        if (isbn != null && !isbn.isBlank() && isbn.trim().length() != 13) {
            throw new InvalidBookDataException("ISBN must be exactly 13 characters long");
        }
    }

    private static void validatePublishedYear(Short publishedYear) {
        if (publishedYear != null) {
            int currentYear = LocalDateTime.now().getYear();
            if (publishedYear < 0 || publishedYear > currentYear) {
                throw new InvalidBookDataException("Published year must be a valid year");
            }
        }
    }

    private static void validateQuantities(int total, int available) {
        if (total < 0) {
            throw new InvalidBookDataException("Total quantity cannot be negative");
        }
        if (available < 0) {
            throw new InvalidBookDataException("Available quantity cannot be negative");
        }
        if (available > total) {
            throw new InvalidBookDataException("Available quantity cannot exceed total quantity");
        }
    }

    private static String normalizeNullable(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    // ==================== GETTERS ONLY (NO DANGEROUS SETTERS) ====================

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public Short getPublishedYear() {
        return publishedYear;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}