package org.example.librarymanagement.infrastructure.web.book;

import org.example.librarymanagement.infrastructure.file.FileStorageService;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class CreateBookController {

    private final CreateBookUseCase createBookUseCase;
    private final FileStorageService fileStorageService;

    public CreateBookController(
            CreateBookUseCase createBookUseCase,
            FileStorageService fileStorageService
    ) {
        this.createBookUseCase = createBookUseCase;
        this.fileStorageService = fileStorageService;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BookResult> createBook(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam String isbn,
            @RequestParam Long categoryId,
            @RequestParam int totalQuantity,
            @RequestParam MultipartFile coverImage,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Integer publishedYear,
            @RequestParam(required = false) String shelfLocation
    ) {
        String imageUrl = null;
        
        try {
            // Upload ảnh an toàn
            imageUrl = fileStorageService.storeBookImage(coverImage);

            CreateBookCommand command = new CreateBookCommand(
                    title,
                    author,
                    isbn,
                    description,
                    imageUrl,
                    publisher,
                    publishedYear,
                    shelfLocation,
                    totalQuantity,
                    categoryId
            );

            // Delegate sang Core Application
            BookResult result = createBookUseCase.createBook(command);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result);

        } catch (Exception e) {
            // Rollback/Compensation: Xóa ảnh mồ côi nếu DB hoặc UseCase có lỗi
            if (imageUrl != null) {
                fileStorageService.deleteFile(imageUrl);
            }
            throw e; 
        }
    }

    @GetMapping
    public ResponseEntity<List<BookResult>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<BookResult> books = createBookUseCase.getAllBooks(page, size);
        return ResponseEntity.ok(books);
    }
}