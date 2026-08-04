package org.example.librarymanagement.infrastructure.web.book;


import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.infrastructure.file.FileStorageService;
import org.example.librarymanagement.port.inbound.managebook.CreateBookCommand;
import org.example.librarymanagement.port.inbound.managebook.ManageBookUseCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/books")
public class BookController {


    private final ManageBookUseCase manageBookUseCase;

    private final FileStorageService fileStorageService;



    public BookController(
            ManageBookUseCase manageBookUseCase,
            FileStorageService fileStorageService
    ) {

        this.manageBookUseCase = manageBookUseCase;
        this.fileStorageService = fileStorageService;

    }




    // CREATE BOOK + UPLOAD IMAGE

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Book> createBook(

            @RequestParam String title,

            @RequestParam String author,

            @RequestParam String isbn,

            @RequestParam UUID categoryId,

            @RequestParam int totalQuantity,

            @RequestParam MultipartFile coverImage

    ) {


        // 1. Upload ảnh

        String imageUrl =
                fileStorageService.storeBookImage(coverImage);



        // 2. Tạo command

        CreateBookCommand command =
                new CreateBookCommand(

                        title,

                        author,

                        isbn,

                        null,

                        imageUrl,

                        null,

                        null,

                        null,

                        totalQuantity,

                        categoryId

                );



        // 3. Tạo sách

        Book book =
                manageBookUseCase.createBook(command);



        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(book);

    }





    // GET ALL BOOKS

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {


        List<Book> books =
                manageBookUseCase.getAllBooks();


        return ResponseEntity.ok(books);

    }

}