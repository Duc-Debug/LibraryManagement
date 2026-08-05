package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import org.example.librarymanagement.domain.exceptions.DomainException;

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

    public Book() {
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Book(Long id, String title, String author, String isbn, String description,
            String coverImageUrl, String publisher, Short publishedYear,
            String shelfLocation, int totalQuantity, int availableQuantity,
            Long categoryId, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {

        validateNotBlank(title, "Title is not null");
        validateNotBlank(author, "Author is not null");
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

    public boolean isAvailableForBorrow() {
        return this.active && this.availableQuantity > 0;
    }

    public void decreaseAvailableQuantity() {
        if (!isAvailableForBorrow()) {
            throw new DomainException("Book now is not have more to borrow.");
        }
        this.availableQuantity--;
        touch();
    }

    public void increaseAvailableQuantity() {
        if (this.availableQuantity >= this.totalQuantity) {
            throw new DomainException("Avaiable quantity is not more than total quantity book.");
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

     /**
     * Hành vi nghiệp vụ: Nhập thêm số lượng sách mới vào kho
     * @param addedQuantity Số lượng sách nhập thêm (phải > 0)
     */
    public void restock(int addedQuantity) {
        if (addedQuantity <= 0) {
            throw new DomainException("Số lượng sách nhập thêm phải lớn hơn 0.");
        }
        this.totalQuantity += addedQuantity;
        this.availableQuantity += addedQuantity;
        touch();
    }
    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Short getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Short publishedYear) {
        this.publishedYear = publishedYear;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
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

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
        return Objects.equals(bookId, book.bookId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookId);
    }

    private void validateNotBlank(String value, String errorMEsseage) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException(errorMEsseage);
        }
    }

    private void validateQuantities(int total, int available) {
        if (total < 0) {
            throw new DomainException("Tổng số lượng sách không được nhỏ hơn 0.");
        }
        if (available < 0) {
            throw new DomainException("Số lượng sách có sẵn không được nhỏ hơn 0.");
        }
        if (available > total) {
            throw new DomainException("Số lượng sách có sẵn không được vượt quá tổng số lượng.");
        }
    }
}
