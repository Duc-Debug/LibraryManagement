package org.example.librarymanagement.domain.entity;
import java.rmi.server.UID;
import java.time.LocalDateTime;
import org.example.librarymanagement.domain.exceptions.DomainException;
public class borrow_details {
    private UID id;
    private UID borrow_slip_id;
    private UID book_id;
    private LocalDateTime return_at;
    private UID return_by_user_id;
    private String fine_reason;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public borrow_details() {}

    public borrow_details(UID id, UID borrow_slip_id, UID book_id, LocalDateTime return_at, UID return_by_user_id, String fine_reason, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.borrow_slip_id = borrow_slip_id;
        this.book_id = book_id;
        this.return_at = return_at;
        this.return_by_user_id = return_by_user_id;
        this.fine_reason = fine_reason;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public UID getId() {
        return id;
    }
    public void setId(UID id) {
        if(id == null) {
            throw new DomainException("ID cannot be null");
        }
      this.id = id;
    }
    
    public UID getBorrow_slip_id() {
        return borrow_slip_id;
    }
    public void setBorrow_slip_id(UID borrow_slip_id) {
        if(borrow_slip_id == null) {
            throw new DomainException("Borrow slip ID cannot be null");
        }
        this.borrow_slip_id = borrow_slip_id;
    }

    public UID getBook_id() {
        return book_id;
    }
    public void setBook_id(UID book_id) {
        if(book_id == null) {
            throw new DomainException("Book ID cannot be null");
        }
        this.book_id = book_id;
    }

    public LocalDateTime getReturn_at() {
        return return_at;
    }
    public void setReturn_at(LocalDateTime return_at) {
        if( created_at != null && return_at != null && return_at.isBefore(created_at)) {
            throw new DomainException("Return date cannot be before created date");
        }
        this.return_at = return_at;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }
    public void setCreated_at(LocalDateTime created_at) {
        if(created_at == null) {
            throw new DomainException("Created date cannot be null");
        }
        this.created_at = created_at;
    }

    public UID getReturn_by_user_id() {
        return return_by_user_id;
    }
    public void setReturn_by_user_id(UID return_by_user_id) {
        if(return_at != null && return_by_user_id == null) {
            throw new DomainException("Return by user ID cannot be null when return date is set");
        }
        this.return_by_user_id = return_by_user_id;
    }

    public String getFine_reason() {
        return fine_reason;
    }
    public void setFine_reason(String fine_reason) {
        if(fine_reason == null) {
            throw new DomainException("Fine reason cannot be null");
        }
        if(fine_reason.length() > 255) {
            throw new DomainException("Fine reason cannot exceed 255 characters");
        }
        if(fine_reason.trim().isEmpty()) {
            throw new DomainException("Fine reason cannot be empty");
        }
        this.fine_reason = fine_reason;


   
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
    }
    public void setUpdated_at(LocalDateTime updated_at) {
        if(updated_at != null && created_at != null && updated_at.isBefore(created_at)) {
            throw new DomainException("Updated date cannot be before created date");
        }
        this.updated_at = updated_at;
    }




    
}
