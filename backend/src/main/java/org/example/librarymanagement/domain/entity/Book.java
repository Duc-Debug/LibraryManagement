package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.book.InvalidBookDataException;

public class Book {
    private Long bookId;
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
            Long categoryId
    ) {
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
                now
        );
    }

    // 2. Full-Args Constructor dùng khi Re-constitute Entity từ Database (Persistence Mapper)
    public Book(Long id, String title, String author, String isbn, String description, 
                String coverImageUrl, String publisher, Short publishedYear, 
                String shelfLocation, int totalQuantity, int availableQuantity, 
                Long categoryId, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {

        validateNotBlank(title, "Title must not be blank");
        validateNotBlank(author, "Author must not be blank");
        validateQuantities(totalQuantity, availableQuantity);

        this.bookId = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.publisher = publisher;
        this.publishedYear = publishedYear;
        this.shelfLocation = shelfLocation;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
        this.categoryId = categoryId;
        this.active = active;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    // ==================== DOMAIN BEHAVIORS & INVARIANTS ====================

    private void validateNotBlank(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidBookDataException(errorMessage);
        }
    }

    private void validateQuantities(int total, int available) {
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

    public void updateInfo(
            String title,
            String author,
            String isbn,
            String description,
            String coverImageUrl,
            String publisher,
            Short publishedYear,
            String shelfLocation,
            Long categoryId
    ) {
        validateNotBlank(title, "Title must not be blank");
        validateNotBlank(author, "Author must not be blank");

        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.publisher = publisher;
        this.publishedYear = publishedYear;
        this.shelfLocation = shelfLocation;
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
            throw new InvalidBookDataException("Sách hiện không còn sẵn để mượn.");
        }
        this.availableQuantity--;
        touch();
    }

    public void increaseAvailableQuantity() {
        if (this.availableQuantity >= this.totalQuantity) {
            throw new InvalidBookDataException("Số lượng có sẵn không thể vượt quá tổng số lượng.");
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

    // ==================== GETTERS ONLY (NO DANGEROUS SETTERS) ====================

    public Long getBookId() {
        return bookId;
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    public void updateInfor(String title, String author, String newIsbn, String description, 
                     String coverImageUrl, String publisher, Short publishedYear, 
                     String shelfLocation, int newTotalQuantity,  
                     Long categoryId) {

                        int currentYear = LocalDateTime.now().getYear();

                        //validation du lieu co ban
                        if (title == null || title.trim().isEmpty()) {
                            throw new InvalidBookDataException("Title cannot be null or empty.");
                        }
                        if( newIsbn != null && !newIsbn.trim().isEmpty() && newIsbn.length() != 13) {
                            throw new InvalidBookDataException("ISBN must be 13 characters long.");
                        }
                        if(categoryId == null) {
                            throw new InvalidBookDataException("Category ID cannot be null.");
                        }
                        if(publishedYear != null && (publishedYear < 0 || publishedYear > currentYear)) {
                            throw new InvalidBookDataException("Published year must be a valid year.");
                        }

                       

                        // validate khong cho phep availableQuantity > totalQuantity
                        int borrowedQuantity = this.totalQuantity - this.availableQuantity;
                        if (newTotalQuantity < borrowedQuantity) {
                           throw new DomainException(
                            "New total quantity(" + newTotalQuantity + ") cannot be less than the number of borrowed books (" + borrowedQuantity + ")."
                           );
                        }

                        //update thong tin sach
                        this.title = title.trim();
                        this.author = author != null ? author.trim() : null;
                        this.isbn = newIsbn;
                        this.description = description != null ? description.trim() : null;
                        this.coverImageUrl = coverImageUrl != null ? coverImageUrl.trim() : null;
                        this.publisher = publisher != null ? publisher.trim() : null;
                        this.publishedYear = publishedYear;
                        this.shelfLocation = shelfLocation != null ? shelfLocation.trim() : null;
                        this.totalQuantity = newTotalQuantity;
                        this.availableQuantity = newTotalQuantity - borrowedQuantity;
                        this.categoryId = categoryId;
                        this.active = active;
                        this.updatedAt = LocalDateTime.now();

                    

       
    }
}
