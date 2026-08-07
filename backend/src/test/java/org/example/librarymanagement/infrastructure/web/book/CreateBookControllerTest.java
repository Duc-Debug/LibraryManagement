package org.example.librarymanagement.infrastructure.web.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.example.librarymanagement.infrastructure.file.FileStorageService;
import org.example.librarymanagement.port.inbound.book.BookResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

class CreateBookControllerTest {

    private CreateBookUseCase createBookUseCase;
    private FileStorageService fileStorageService;
    private MockMvc mockMvc;

    private BookResult mockBookResult;

    @BeforeEach
    void setUp() {
        // Khởi tạo mock objects
        createBookUseCase = mock(CreateBookUseCase.class);
        fileStorageService = mock(FileStorageService.class);

        // Setup MockMvc độc lập
        mockMvc = MockMvcBuilders
                .standaloneSetup(new CreateBookController(createBookUseCase, fileStorageService))
                .build();

        // Khởi tạo DTO mock thay cho Domain Entity theo chuẩn mới
        mockBookResult = new BookResult(
                1L,
                "Clean Code",
                "Robert Martin",
                "9780132350884",
                "/uploads/books/image.jpg",
                10,
                10,
                1L,
                true
        );
    }

    @Test
    void createBookSuccessfully() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "coverImage",
                "book.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image".getBytes()
        );

        when(fileStorageService.storeBookImage(any(MultipartFile.class)))
                .thenReturn("/uploads/books/image.jpg");
                
        when(createBookUseCase.createBook(any(CreateBookCommand.class)))
                .thenReturn(mockBookResult);

        mockMvc.perform(multipart("/api/books")
                        .file(image)
                        .param("title", "Clean Code")
                        .param("author", "Robert Martin")
                        .param("isbn", "9780132350884")
                        .param("categoryId", "1")
                        .param("totalQuantity", "10")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.author").value("Robert Martin"))
                .andExpect(jsonPath("$.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.coverImageUrl").value("/uploads/books/image.jpg"));

        verify(fileStorageService).storeBookImage(any(MultipartFile.class));
        
        // Kiểm tra chặt chẽ tham số truyền vào UseCase
        verify(createBookUseCase).createBook(argThat(command ->
                command.title().equals("Clean Code")
                && command.author().equals("Robert Martin")
                && command.isbn().equals("9780132350884")
                && command.categoryId().equals(1L)
                && command.totalQuantity() == 10
        ));
    }

    @Test
    void getAllBooks_ReturnsPagedResults() throws Exception {
        when(createBookUseCase.getAllBooks(anyInt(), anyInt())).thenReturn(List.of(mockBookResult));

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].isbn").value("9780132350884"));
    }
}