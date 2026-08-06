package org.example.librarymanagement.infrastructure.web.librarians;

import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/librarians/books")
@RequiredArgsConstructor
public class BookManagementController {
    private final DeleteBookUseCase deleteBookUseCase;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id){
        deleteBookUseCase.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<Void> hideBook(@PathVariable Long id){
        deleteBookUseCase.hideBook(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unhide")
    public ResponseEntity<Void> unhideBook(@PathVariable Long id){
        deleteBookUseCase.unhideBook(id);
        return ResponseEntity.ok().build();
    }
}
