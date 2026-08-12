package org.example.librarymanagement.infrastructure.web.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.port.dtos.book.BookAvailabilityStatus;
import org.example.librarymanagement.port.dtos.book.BookFilterQuery;
import org.example.librarymanagement.port.dtos.book.BookResponseDto;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.inbound.book.DeleteBookUseCase;
import org.example.librarymanagement.port.inbound.book.GetBooksUseCase;
import org.example.librarymanagement.port.inbound.book.ReplenishBookStockUseCase;
import org.example.librarymanagement.port.inbound.book.UpdateBookUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BookManagementControllerTest {

    private DeleteBookUseCase deleteBookUseCase;
    private GetBooksUseCase getBooksUseCase;
    private UpdateBookUseCase updateBookUseCase;
    private ReplenishBookStockUseCase replenishBookStockUseCase;

    private MockMvc mockMvc;
    private BookResponseDto mockBookResponseDto;

    @BeforeEach
    void setUp() {
        deleteBookUseCase = mock(DeleteBookUseCase.class);
        getBooksUseCase = mock(GetBooksUseCase.class);
        updateBookUseCase = mock(UpdateBookUseCase.class);
        replenishBookStockUseCase = mock(ReplenishBookStockUseCase.class);

        BookManagementController controller = new BookManagementController(
                deleteBookUseCase,
                getBooksUseCase,
                updateBookUseCase,
                replenishBookStockUseCase
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockBookResponseDto = new BookResponseDto(
                1L,
                "Clean Architecture",
                "Robert C. Martin",
                "9780134494166",
                "Software Architecture Description",
                "https://example.com/cover.jpg",
                "Prentice Hall",
                (short) 2017,
                "Shelf A1",
                10,
                8,
                1L,
                "Công nghệ & Phần mềm",
                true,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("GET /api/librarians/books: Lọc sách theo keyword, categoryId và availability thành công")
    void getBooks_WithFilters_Success() throws Exception {
        PageResult<BookResponseDto> pageResult = PageResult.of(List.of(mockBookResponseDto), 0, 10, 1);

        when(getBooksUseCase.getBooks(any(BookFilterQuery.class))).thenReturn(pageResult);

        mockMvc.perform(get("/api/librarians/books")
                        .param("page", "0")
                        .param("size", "10")
                        .param("keyword", "Clean")
                        .param("categoryId", "1")
                        .param("availability", "AVAILABLE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].bookId").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Clean Architecture"))
                .andExpect(jsonPath("$.content[0].categoryName").value("Công nghệ & Phần mềm"));

        verify(getBooksUseCase).getBooks(new BookFilterQuery(0, 10, "Clean", 1L, BookAvailabilityStatus.AVAILABLE));
    }

    @Test
    @DisplayName("DELETE /api/librarians/books/{id}: Xóa sách thành công")
    void deleteBook_Success() throws Exception {
        mockMvc.perform(delete("/api/librarians/books/1"))
                .andExpect(status().isNoContent());

        verify(deleteBookUseCase).deleteBook(1L);
    }

    @Test
    @DisplayName("PATCH /api/librarians/books/{id}/hide: Ẩn sách thành công")
    void hideBook_Success() throws Exception {
        mockMvc.perform(patch("/api/librarians/books/1/hide"))
                .andExpect(status().isOk());

        verify(deleteBookUseCase).hideBook(1L);
    }

    @Test
    @DisplayName("PATCH /api/librarians/books/{id}/unhide: Khôi phục sách thành công")
    void unhideBook_Success() throws Exception {
        mockMvc.perform(patch("/api/librarians/books/1/unhide"))
                .andExpect(status().isOk());

        verify(deleteBookUseCase).unhideBook(1L);
    }
}
