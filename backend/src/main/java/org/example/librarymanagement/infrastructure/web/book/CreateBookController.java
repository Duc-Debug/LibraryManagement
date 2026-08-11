package org.example.librarymanagement.infrastructure.web.book;

import java.io.IOException;
import java.io.InputStream;

import org.example.librarymanagement.port.dtos.book.BookResponseDto;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class CreateBookController {

    private static final int MAX_PAGE_SIZE = 100;

    private final CreateBookUseCase createBookUseCase;
    private final GetBooksUseCase getBooksUseCase;

    public CreateBookController(
            CreateBookUseCase createBookUseCase,
            GetBooksUseCase getBooksUseCase
    ) {
        this.createBookUseCase = createBookUseCase;
        this.getBooksUseCase = getBooksUseCase;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('LIBRARIAN')")
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<BookResult> createBook(
        @Valid @ModelAttribute CreateBookRequest request,
        @RequestParam(required = false) MultipartFile coverImage
) throws IOException {

    InputStream imageStream =
            (coverImage != null && !coverImage.isEmpty())
                    ? coverImage.getInputStream()
                    : null;

    try (InputStream stream = imageStream) {

        String originalFilename =
                coverImage != null
                        ? coverImage.getOriginalFilename()
                        : null;

        long size =
                coverImage != null
                        ? coverImage.getSize()
                        : 0;

        CreateBookCommand command = new CreateBookCommand(
                request.title(),
                request.author(),
                request.isbn(),
                request.description(),
                stream,
                originalFilename,
                size,
                request.publisher(),
                request.publishedYear(),
                request.shelfLocation(),
                request.totalQuantity(),
                request.categoryId()
        );

        BookResult result = createBookUseCase.createBook(command);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }
}
    @GetMapping
    public ResponseEntity<PageResult<BookResponseDto>> getBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ) {
        // 1. Enforce pagination bounds tại HTTP boundary
        int validPage = Math.max(0, page);
        int validSize = Math.min(MAX_PAGE_SIZE, Math.max(1, size));

        // 2. Sử dụng duy nhất GetBooksUseCase cho chức năng đọc danh sách sách
        PageResult<BookResponseDto> books = getBooksUseCase.getBooks(validPage, validSize, keyword);
        return ResponseEntity.ok(books);
    }
}
