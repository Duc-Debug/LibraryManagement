package org.example.librarymanagement.infrastructure.web.category;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.example.librarymanagement.application.category.exception.CategoryInUseException;
import org.example.librarymanagement.application.category.exception.CategoryNotFoundException;
import org.example.librarymanagement.application.category.exception.DuplicateCategoryNameException;
import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.infrastructure.web.exception.GlobalExceptionHandler;
import org.example.librarymanagement.port.inbound.category.CategoryManagementUseCase;
import org.example.librarymanagement.port.inbound.category.CategoryResult;
import org.example.librarymanagement.port.inbound.category.CreateCategoryCommand;
import org.example.librarymanagement.port.inbound.category.UpdateCategoryCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class CategoryControllerTest {

    private CategoryManagementUseCase categoryManagementUseCase;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        categoryManagementUseCase =
                mock(CategoryManagementUseCase.class);

        LocalValidatorFactoryBean validator =
                new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new CategoryController(
                        categoryManagementUseCase,
                        new CategoryWebMapper()
                ))
                .setControllerAdvice(
                        new CategoryExceptionHandler(),
                        new GlobalExceptionHandler()
                )
                .setValidator(validator)
                .build();
    }

    @Test
    void createCategoryReturnsCreatedResponse() throws Exception {
        when(categoryManagementUseCase.createCategory(
                new CreateCategoryCommand(
                        "Science",
                        "Science books"
                )
        )).thenReturn(categoryResult(
                1L,
                "Science",
                "Science books",
                true
        ));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Science",
                                  "description": "Science books"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/categories/1"
                ))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Science"))
                .andExpect(jsonPath("$.description").value("Science books"))
                .andExpect(jsonPath("$.active").value(true));

        verify(categoryManagementUseCase).createCategory(
                new CreateCategoryCommand(
                        "Science",
                        "Science books"
                )
        );
    }

    @Test
    void createCategoryRejectsBlankName() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": " ",
                                  "description": "Science books"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value(
                        containsString("Category name must not be blank")
                ));
    }

    @Test
    void createCategoryMapsDuplicateNameToConflict() throws Exception {
        when(categoryManagementUseCase.createCategory(
                new CreateCategoryCommand("Science", null)
        )).thenThrow(new DuplicateCategoryNameException("Science"));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Science"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(
                        "DUPLICATE_CATEGORY_NAME"
                ));
    }

    @Test
    void updateCategoryReturnsUpdatedResponse() throws Exception {
        when(categoryManagementUseCase.updateCategory(
                new UpdateCategoryCommand(
                        1L,
                        "Science Fiction",
                        null,
                        false
                )
        )).thenReturn(categoryResult(
                1L,
                "Science Fiction",
                null,
                false
        ));

        mockMvc.perform(patch("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Science Fiction",
                                  "active": false
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Science Fiction"))
                .andExpect(jsonPath("$.active").value(false));

        verify(categoryManagementUseCase).updateCategory(
                new UpdateCategoryCommand(
                        1L,
                        "Science Fiction",
                        null,
                        false
                )
        );
    }

    @Test
    void updateCategoryMapsEmptyPatchToBadRequest() throws Exception {
        when(categoryManagementUseCase.updateCategory(
                new UpdateCategoryCommand(
                        1L,
                        null,
                        null,
                        null
                )
        )).thenThrow(
                new ValidationException(
                        "At least one category field must be provided"
                )
        );

        mockMvc.perform(patch("/api/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void getCategoryByIdMapsMissingCategoryToNotFound() throws Exception {
        when(categoryManagementUseCase.getCategoryById(404L))
                .thenThrow(new CategoryNotFoundException(404L));

        mockMvc.perform(get("/api/categories/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CATEGORY_NOT_FOUND"));
    }

    @Test
    void getCategoryByIdRejectsNonNumericId() throws Exception {
        mockMvc.perform(get("/api/categories/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value(
                        "Category id must be a valid number"
                ));
    }

    @Test
    void getAllCategoriesReturnsMappedResponses() throws Exception {
        when(categoryManagementUseCase.getAllCategories())
                .thenReturn(List.of(
                        categoryResult(1L, "Literature", null, true),
                        categoryResult(2L, "Science", null, false)
                ));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Literature"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Science"))
                .andExpect(jsonPath("$[1].active").value(false));
    }

    @Test
    void deleteCategoryReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryManagementUseCase).deleteCategory(1L);
    }

    @Test
    void deleteCategoryMapsCategoryInUseToConflict() throws Exception {
        doThrow(new CategoryInUseException(1L))
                .when(categoryManagementUseCase)
                .deleteCategory(1L);

        mockMvc.perform(delete("/api/categories/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("CATEGORY_IN_USE"));
    }

    private static CategoryResult categoryResult(
            Long id,
            String name,
            String description,
            boolean active
    ) {
        LocalDateTime createdAt =
                LocalDateTime.of(2026, 8, 5, 9, 0);
        LocalDateTime updatedAt =
                LocalDateTime.of(2026, 8, 5, 9, 30);

        return new CategoryResult(
                id,
                name,
                description,
                active,
                createdAt,
                updatedAt
        );
    }
}
