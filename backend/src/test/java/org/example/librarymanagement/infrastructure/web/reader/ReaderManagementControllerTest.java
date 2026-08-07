package org.example.librarymanagement.infrastructure.web.reader;

import java.util.List;

import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.infrastructure.web.exception.GlobalExceptionHandler;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.ReaderResult;
import org.example.librarymanagement.port.inbound.reader.ReaderManagementUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class ReaderManagementControllerTest {

    private ReaderManagementUseCase readerManagementUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        readerManagementUseCase = mock(ReaderManagementUseCase.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReaderManagementController(readerManagementUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/readers trả về HTTP 201 Created khi gửi dữ liệu tạo bạn đọc hợp lệ")
    void createReader_Returns201_WhenValid() throws Exception {
        ReaderResult mockResult = new ReaderResult(
                1L, "RD-260805-1001", "Nguyễn Văn A", "nva@gmail.com", "0987654321", "Hà Nội",
                CardStatus.ACTIVE, null, null, "Thủ thư 1"
        );

        when(readerManagementUseCase.createReader(any(CreateReaderCommand.class))).thenReturn(mockResult);

        String jsonBody = """
                {
                    "name": "Nguyễn Văn A",
                    "email": "nva@gmail.com",
                    "phoneNumber": "0987654321",
                    "address": "Hà Nội"
                }
                """;

        mockMvc.perform(post("/api/v1/readers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").value("RD-260805-1001"))
                .andExpect(jsonPath("$.name").value("Nguyễn Văn A"))
                .andExpect(jsonPath("$.createdByName").value("Thủ thư 1"));

        verify(readerManagementUseCase).createReader(any(CreateReaderCommand.class));
    }

    @Test
    @DisplayName("GET /api/v1/readers?page=0&size=10 trả về HTTP 200 OK cùng dữ liệu phân trang")
    void getAllReaders_Paginated_Returns200() throws Exception {
        ReaderResult mockResult = new ReaderResult(
                1L, "RD-260805-1001", "Nguyễn Văn A", "nva@gmail.com", "0987654321", "Hà Nội",
                CardStatus.ACTIVE, null, null, "Thủ thư 1"
        );

        PageResult<ReaderResult> pageResult = PageResult.of(List.of(mockResult), 0, 10, 1);
        when(readerManagementUseCase.getAllReaders(anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/v1/readers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardNumber").value("RD-260805-1001"));

        verify(readerManagementUseCase).getAllReaders(0, 10);
    }

    @Test
    @DisplayName("GET /api/v1/readers without query params returns default paginated response")
    void getAllReaders_DefaultPagination_ReturnsPageResult() throws Exception {
        ReaderResult mockResult = new ReaderResult(
                1L, "RD-260805-1001", "Nguyen Van A", "nva@gmail.com", "0987654321", "Ha Noi",
                CardStatus.ACTIVE, null, null, "Thu thu 1"
        );

        PageResult<ReaderResult> pageResult = PageResult.of(List.of(mockResult), 0, 20, 1);
        when(readerManagementUseCase.getAllReaders(0, 20)).thenReturn(pageResult);

        mockMvc.perform(get("/api/v1/readers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardNumber").value("RD-260805-1001"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(1));

        verify(readerManagementUseCase).getAllReaders(0, 20);
    }
}
