package org.example.librarymanagement.infrastructure.web.book;

import java.io.IOException;
import java.io.InputStream;

import org.example.librarymanagement.infrastructure.web.book.dto.ReplenishStockRequest;
import org.example.librarymanagement.infrastructure.web.book.dto.UpdateBookRequest;
import org.example.librarymanagement.port.dtos.book.BookAvailabilityStatus;
import org.example.librarymanagement.port.dtos.book.BookFilterQuery;
import org.example.librarymanagement.port.dtos.book.BookResponseDto;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.book.ReplenishBookStockCommand;
import org.example.librarymanagement.port.dtos.book.UpdateBookCommand;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockUseCase;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController("librarianBookManagementController")
@RequestMapping("/api/librarians/books")
@RequiredArgsConstructor
public class BookManagementController {

    private final DeleteBookUseCase deleteBookUseCase;
    private final GetBooksUseCase getBooksUseCase;
    private final UpdateBookUseCase updateBookUseCase;
    private final ReplenishBookStockUseCase replenishBookStockUseCase;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        deleteBookUseCase.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/hide")
    public ResponseEntity<Void> hideBook(@PathVariable Long id) {
        deleteBookUseCase.hideBook(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/unhide")
    public ResponseEntity<Void> unhideBook(@PathVariable Long id) {
        deleteBookUseCase.unhideBook(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<PageResult<BookResponseDto>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "ALL") BookAvailabilityStatus availability) {

        // Gom tất cả vào DTO BookFilterQuery
        BookFilterQuery query = new BookFilterQuery(page, size, keyword, categoryId, availability);

        PageResult<BookResponseDto> result = getBooksUseCase.getBooks(query);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        BookResponseDto responseDto = getBooksUseCase.getBookById(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResult> updateBookWithMultipart(
            @PathVariable("id") Long bookId,
            @Valid @ModelAttribute UpdateBookRequest request,
            @RequestParam(required = false) MultipartFile coverImage) throws IOException {

        InputStream imageStream = (coverImage != null && !coverImage.isEmpty())
                ? coverImage.getInputStream()
                : null;

        try (InputStream stream = imageStream) {
            String originalFilename = coverImage != null ? coverImage.getOriginalFilename() : null;
            long size = coverImage != null ? coverImage.getSize() : 0;

            UpdateBookCommand commandToExecute = new UpdateBookCommand(
                    bookId,
                    request.title(),
                    request.author(),
                    request.isbn(),
                    request.description(),
                    request.coverImageUrl(),
                    request.publisher(),
                    request.publishedYear(),
                    request.shelfLocation(),
                    request.totalQuantity(),
                    request.categoryId(),
                    stream,
                    originalFilename,
                    size
            );
            BookResult result = updateBookUseCase.updateBook(commandToExecute);
            return ResponseEntity.ok(result);
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BookResult> updateBook(
            @PathVariable("id") Long bookId,
            @Valid @RequestBody UpdateBookRequest request) {

        UpdateBookCommand commandToExecute = new UpdateBookCommand(
                bookId,
                request.title(),
                request.author(),
                request.isbn(),
                request.description(),
                request.coverImageUrl(),
                request.publisher(),
                request.publishedYear(),
                request.shelfLocation(),
                request.totalQuantity(),
                request.categoryId()
        );
        BookResult result = updateBookUseCase.updateBook(commandToExecute);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/replenish")
    public ResponseEntity<BookResult> replenishStock(
            @PathVariable Long id,
            @Valid @RequestBody ReplenishStockRequest request) {
        BookResult result = replenishBookStockUseCase.replenishStock(
                new ReplenishBookStockCommand(id, request.quantityToAdd())
        );
        return ResponseEntity.ok(result);
    }
}
