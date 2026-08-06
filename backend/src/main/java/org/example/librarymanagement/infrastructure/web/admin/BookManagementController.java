package org.example.librarymanagement.infrastructure.web.admin;


import org.example.librarymanagement.port.inbound.manage.BookResult;
import org.example.librarymanagement.port.inbound.manage.UpdateBookCommand;
import org.example.librarymanagement.port.inbound.manage.UpdateBookUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/manage/books")
@RequiredArgsConstructor

public class BookManagementController {
    private final UpdateBookUseCase updateBookUseCase;



    @PutMapping("/{id}")    
    public ResponseEntity<BookResult> updateBook(
        @PathVariable("id") Long bookId,
        @Valid @RequestBody UpdateBookCommand command)
        {

            // dam bao bookid trne path match voi command
            UpdateBookCommand commandToExecute = new UpdateBookCommand(
                bookId,
                command.title(),
                command.author(),
                command.isbn(),
                command.description(),
                command.coverImageUrl(),
                command.publisher(),
                command.publishedYear(),
                command.shelfLocation(),
                command.totalQuantity(),
                command.categoryId()
             ); 
            BookResult result = updateBookUseCase.updateBook(commandToExecute);
            return ResponseEntity.ok(result);
        }
    }
    
    
    
    

