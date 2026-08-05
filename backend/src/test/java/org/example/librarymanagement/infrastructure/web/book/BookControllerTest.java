package org.example.librarymanagement.infrastructure.web.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import org.example.librarymanagement.domain.entity.Book;
import org.example.librarymanagement.infrastructure.file.FileStorageService;
import org.example.librarymanagement.port.inbound.managebook.ManageBookUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;


class BookControllerTest {


    private ManageBookUseCase manageBookUseCase;

    private FileStorageService fileStorageService;

    private MockMvc mockMvc;


    @BeforeEach
    void setUp() {


        manageBookUseCase =
                mock(ManageBookUseCase.class);


        fileStorageService =
                mock(FileStorageService.class);



        mockMvc =
                MockMvcBuilders
                        .standaloneSetup(
                                new BookController(
                                        manageBookUseCase,
                                        fileStorageService
                                )
                        )
                        .build();

    }



    @Test
    void createBookSuccessfully() throws Exception {


        Book book = new Book();



        when(fileStorageService.storeBookImage(any(MultipartFile.class)))
                .thenReturn(
                        "/uploads/books/image.jpg"
                );



        when(manageBookUseCase.createBook(any()))
                .thenReturn(book);



        MockMultipartFile image =
                new MockMultipartFile(
                        "coverImage",
                        "book.jpg",
                        "image/jpeg",
                        "fake image".getBytes()
                );



        mockMvc.perform(
                multipart("/api/books")
                        .file(image)

                        .param(
                                "title",
                                "Clean Code"
                        )

                        .param(
                                "author",
                                "Robert Martin"
                        )

                        .param(
                                "isbn",
                                "9780132350884"
                        )

                        .param(
                                "categoryId",
                                "1"
                        )

                        .param(
                                "totalQuantity",
                                "10"
                        )
        )

        .andExpect(status().isCreated());



        verify(fileStorageService)
                .storeBookImage(any(MultipartFile.class));



        verify(manageBookUseCase)
        .createBook(argThat(command ->
                command.title().equals("Clean Code")
                && command.author().equals("Robert Martin")
                && command.isbn().equals("9780132350884")
                && command.categoryId().equals(1L)
                && command.totalQuantity() == 10
        ));
    }

}