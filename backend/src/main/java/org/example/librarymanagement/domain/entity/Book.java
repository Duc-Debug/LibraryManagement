package org.example.librarymanagement.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.example.librarymanagement.domain.exceptions.DomainException;
public class Book {
    private UUID bookId;
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
    public UUID categoryId;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Book() {
        this.bookId = UUID.randomUUID();
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    public Book(UUID id, String title, String author, String isbn, String description, 
                String coverImageUrl, String publisher, Integer publishedYear, 
                String shelfLocation, int totalQuantity, int availableQuantity, 
                UUID categoryId, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.bookId = id != null ? id : UUID.randomUUID();
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
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
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

    public void deactivate(){
        if(this.active){
            this.active = false;
            touch();
        }
    }
    public void active(){
        if(!this.active){
            this.active=true;
            touch();
        }
    }
    private void touch(){
        this.updatedAt = LocalDateTime.now();
    }
    public UUID getBookId() {
        return bookId;
    }
    public void setBookId(UUID bookId) {
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
    public Integer getPublishedYear() {
        return publishedYear;
    }
    public void setPublishedYear(Integer publishedYear) {
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
    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    public int getAvailableQuantity() {
        return availableQuantity;
    }
    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    public UUID getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
    public boolean isActive() {
        return active;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    @Override
    public boolean equals(Object o){
        if(this ==o) return true;
        if(o==null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(bookId,book.bookId);
    }
    @Override
    public int hashCode(){
        return Objects.hash((bookId));
    }
}
