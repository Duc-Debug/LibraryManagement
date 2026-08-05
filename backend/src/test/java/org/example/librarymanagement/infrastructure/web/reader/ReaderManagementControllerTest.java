package org.example.librarymanagement.infrastructure.web.reader;

import java.util.List;

import org.example.librarymanagement.domain.enums.CardStatus;
import org.example.librarymanagement.infrastructure.web.exception.GlobalExceptionHandler;
import org.example.librarymanagement.port.dtos.common.PageResult;
import org.example.librarymanagement.port.dtos.reader.CreateReaderCommand;
import org.example.librarymanagement.port.dtos.reader.CreateReaderResult;
import org.example.librarymanagement.port.inbound.reader.CreateReaderUseCase;
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

    private CreateReaderUseCase createReaderUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        createReaderUseCase = mock(CreateReaderUseCase.class);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ReaderManagementController(createReaderUseCase))
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/readers trả về HTTP 201 Created khi gửi dữ liệu tạo bạn đọc hợp lệ")
    void createReader_Returns201_WhenValid() throws Exception {
        CreateReaderResult mockResult = new CreateReaderResult(
                1L, "RD-260805-1001", "Nguyễn Văn A", "nva@gmail.com", "0987654321", "Hà Nội",
                CardStatus.ACTIVE, null, null, "Thủ thư 1"
        );

        when(createReaderUseCase.createReader(any(CreateReaderCommand.class))).thenReturn(mockResult);

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

        verify(createReaderUseCase).createReader(any(CreateReaderCommand.class));
    }

    @Test
    @DisplayName("GET /api/v1/readers?page=0&size=10 trả về HTTP 200 OK cùng dữ liệu phân trang")
    void getAllReaders_Paginated_Returns200() throws Exception {
        CreateReaderResult mockResult = new CreateReaderResult(
                1L, "RD-260805-1001", "Nguyễn Văn A", "nva@gmail.com", "0987654321", "Hà Nội",
                CardStatus.ACTIVE, null, null, "Thủ thư 1"
        );

        PageResult<CreateReaderResult> pageResult = PageResult.of(List.of(mockResult), 0, 10, 1);
        when(createReaderUseCase.getAllReaders(anyInt(), anyInt())).thenReturn(pageResult);

        mockMvc.perform(get("/api/v1/readers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].cardNumber").value("RD-260805-1001"));

        verify(createReaderUseCase).getAllReaders(0, 10);
    }
}
