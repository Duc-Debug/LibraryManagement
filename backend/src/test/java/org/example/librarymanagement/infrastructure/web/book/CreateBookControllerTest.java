package org.example.librarymanagement.infrastructure.web.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.port.dtos.book.BookResponseDto;
import org.example.librarymanagement.port.dtos.book.BookResult;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.book.CreateBookCommand;
import org.example.librarymanagement.port.inbound.book.CreateBookUseCase;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CreateBookControllerTest {

    private CreateBookUseCase createBookUseCase;
    private GetBooksUseCase getBooksUseCase;
    private MockMvc mockMvc;

    private BookResult mockBookResult;
    private BookResponseDto mockBookResponseDto;

    @BeforeEach
    void setUp() {
        createBookUseCase = mock(CreateBookUseCase.class);
        getBooksUseCase = mock(GetBooksUseCase.class);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CreateBookController(createBookUseCase, getBooksUseCase))
                .build();

        mockBookResult = new BookResult(
                1L,
                "Clean Code",
                "Robert Martin",
                "9780132350884",
                "Description",
                "/uploads/books/image.jpg",
                "Publisher",
                (short) 2008,
                "A1",
                10,
                10,
                1L,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        mockBookResponseDto = new BookResponseDto(
                1L,
                "Clean Code",
                "Robert Martin",
                "9780132350884",
                "Description",
                "/uploads/books/image.jpg",
                "Publisher",
                (short) 2008,
                "A1",
                10,
                10,
                1L,
                "Software",
                true,
                LocalDateTime.now()
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

        verify(createBookUseCase).createBook(argThat(command ->
                command.title().equals("Clean Code")
                && command.author().equals("Robert Martin")
                && command.isbn().equals("9780132350884")
                && command.categoryId().equals(1L)
                && command.totalQuantity() == 10
        ));
    }

    @Test
    void getBooks_ReturnsPagedResults() throws Exception {
        PageResult<BookResponseDto> pageResult = PageResult.of(List.of(mockBookResponseDto), 0, 10, 1);
        when(getBooksUseCase.getBooks(anyInt(), anyInt(), eq(null))).thenReturn(pageResult);

        mockMvc.perform(get("/api/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"))
                .andExpect(jsonPath("$.content[0].isbn").value("9780132350884"));
    }
}