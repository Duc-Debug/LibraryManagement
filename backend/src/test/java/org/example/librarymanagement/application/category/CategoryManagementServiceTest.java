package org.example.librarymanagement.application.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.example.librarymanagement.application.category.exception.CategoryInUseException;
import org.example.librarymanagement.application.category.exception.CategoryNotFoundException;
import org.example.librarymanagement.application.category.exception.DuplicateCategoryNameException;
import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.Category;
import org.example.librarymanagement.port.inbound.category.CategoryResult;
import org.example.librarymanagement.port.inbound.category.CreateCategoryCommand;
import org.example.librarymanagement.port.inbound.category.UpdateCategoryCommand;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.example.librarymanagement.port.outbound.category.CategoryUsagePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryManagementServiceTest {

    @Mock
    private CategoryRepositoryPort categoryRepositoryPort;

    @Mock
    private CategoryUsagePort categoryUsagePort;

    private CategoryManagementService service;

    @BeforeEach
    void setUp() {
        service = new CategoryManagementService(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void constructorRejectsNullRepositoryPort() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new CategoryManagementService(
                        null,
                        categoryUsagePort
                )
        );

        assertEquals(
                "Category repository port must not be null",
                exception.getMessage()
        );
    }

    @Test
    void constructorRejectsNullUsagePort() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new CategoryManagementService(
                        categoryRepositoryPort,
                        null
                )
        );

        assertEquals(
                "Category usage port must not be null",
                exception.getMessage()
        );
    }

    @Test
    void createCategoryNormalizesNameForDuplicateCheckAndSavesCategory() {
        CreateCategoryCommand command = new CreateCategoryCommand(
                "  Literature    Fiction  ",
                "  Book    category  "
        );

        when(categoryRepositoryPort.existsByName("Literature Fiction"))
                .thenReturn(false);

        when(categoryRepositoryPort.save(any(Category.class)))
                .thenAnswer(invocation -> {
                    Category category = invocation.getArgument(0);
                    category.assignId(1L);
                    return category;
                });

        CategoryResult result = service.createCategory(command);

        assertEquals(1L, result.id());
        assertEquals("Literature Fiction", result.name());
        assertEquals("Book category", result.description());
        assertTrue(result.active());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());

        verify(categoryRepositoryPort)
                .existsByName("Literature Fiction");

        verify(categoryRepositoryPort)
                .save(argThat(category ->
                        category.getName().equals("Literature Fiction")
                                && category.getDescription().equals("Book category")
                                && category.isActive()
                ));

        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void createCategoryRejectsDuplicateName() {
        CreateCategoryCommand command = new CreateCategoryCommand(
                "  Literature   Fiction ",
                null
        );

        when(categoryRepositoryPort.existsByName("Literature Fiction"))
                .thenReturn(true);

        assertThrows(
                DuplicateCategoryNameException.class,
                () -> service.createCategory(command)
        );

        verify(categoryRepositoryPort)
                .existsByName("Literature Fiction");

        verify(categoryRepositoryPort, never())
                .save(any(Category.class));

        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void createCategoryRejectsNullCommand() {
        assertThrows(
                ValidationException.class,
                () -> service.createCategory(null)
        );

        verifyNoInteractions(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void createCategoryRejectsBlankNameBeforeRepositoryAccess() {
        CreateCategoryCommand command =
                new CreateCategoryCommand("   ", null);

        assertThrows(
                ValidationException.class,
                () -> service.createCategory(command)
        );

        verifyNoInteractions(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void updateCategoryChangesNameDescriptionAndActiveStatus() {
        Long categoryId = 1L;
        Category category = restoredCategory(
                categoryId,
                "Literature",
                "Old description",
                true
        );

        UpdateCategoryCommand command = new UpdateCategoryCommand(
                categoryId,
                "  Science    Fiction ",
                "  New    description  ",
                false
        );

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryRepositoryPort
                .existsByNameAndIdNot(
                        "Science Fiction",
                        categoryId
                ))
                .thenReturn(false);

        when(categoryRepositoryPort.save(category))
                .thenReturn(category);

        CategoryResult result = service.updateCategory(command);

        assertEquals(categoryId, result.id());
        assertEquals("Science Fiction", result.name());
        assertEquals("New description", result.description());
        assertFalse(result.active());

        verify(categoryRepositoryPort)
                .existsByNameAndIdNot(
                        "Science Fiction",
                        categoryId
                );

        verify(categoryRepositoryPort).save(category);
        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void updateCategorySkipsDuplicateQueryWhenNameIsUnchanged() {
        Long categoryId = 1L;
        Category category = restoredCategory(
                categoryId,
                "Literature",
                "Old description",
                true
        );

        UpdateCategoryCommand command = new UpdateCategoryCommand(
                categoryId,
                "  Literature ",
                "  New    description  ",
                null
        );

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryRepositoryPort.save(category))
                .thenReturn(category);

        CategoryResult result = service.updateCategory(command);

        assertEquals("Literature", result.name());
        assertEquals("New description", result.description());
        assertTrue(result.active());

        verify(categoryRepositoryPort, never())
                .existsByNameAndIdNot(
                        any(String.class),
                        any(Long.class)
                );

        verify(categoryRepositoryPort).save(category);
        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void updateCategoryRejectsNameUsedByAnotherCategory() {
        Long categoryId = 1L;
        Category category = restoredCategory(
                categoryId,
                "Literature",
                null,
                true
        );

        UpdateCategoryCommand command = new UpdateCategoryCommand(
                categoryId,
                "Science",
                null,
                true
        );

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryRepositoryPort
                .existsByNameAndIdNot(
                        "Science",
                        categoryId
                ))
                .thenReturn(true);

        assertThrows(
                DuplicateCategoryNameException.class,
                () -> service.updateCategory(command)
        );

        assertEquals("Literature", category.getName());

        verify(categoryRepositoryPort, never())
                .save(any(Category.class));

        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void updateCategoryRejectsNullCommand() {
        assertThrows(
                ValidationException.class,
                () -> service.updateCategory(null)
        );

        verifyNoInteractions(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void updateCategoryRejectsNullCategoryId() {
        UpdateCategoryCommand command = new UpdateCategoryCommand(
                null,
                "Literature",
                null,
                true
        );

        assertThrows(
                ValidationException.class,
                () -> service.updateCategory(command)
        );

        verifyNoInteractions(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void updateCategoryRejectsEmptyPatch() {
        UpdateCategoryCommand command = new UpdateCategoryCommand(
                1L,
                null,
                null,
                null
        );

        assertThrows(
                ValidationException.class,
                () -> service.updateCategory(command)
        );

        verifyNoInteractions(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void updateCategoryThrowsNotFoundWhenCategoryDoesNotExist() {
        Long categoryId = 1L;

        UpdateCategoryCommand command = new UpdateCategoryCommand(
                categoryId,
                "Literature",
                null,
                true
        );

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> service.updateCategory(command)
        );

        verify(categoryRepositoryPort, never())
                .save(any(Category.class));

        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void getCategoryByIdReturnsMappedResult() {
        Long categoryId = 1L;
        Category category = restoredCategory(
                categoryId,
                "Literature",
                "Book category",
                true
        );

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.of(category));

        CategoryResult result =
                service.getCategoryById(categoryId);

        assertEquals(categoryId, result.id());
        assertEquals("Literature", result.name());
        assertEquals("Book category", result.description());
        assertTrue(result.active());

        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void getCategoryByIdRejectsNullId() {
        assertThrows(
                ValidationException.class,
                () -> service.getCategoryById(null)
        );

        verifyNoInteractions(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void getCategoryByIdThrowsNotFound() {
        Long categoryId = 1L;

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> service.getCategoryById(categoryId)
        );
    }

    @Test
    void getAllCategoriesReturnsMappedResults() {
        Category first = restoredCategory(
                1L,
                "Literature",
                null,
                true
        );

        Category second = restoredCategory(
                2L,
                "Science",
                "Science books",
                false
        );

        when(categoryRepositoryPort.findAll())
                .thenReturn(List.of(first, second));

        List<CategoryResult> results =
                service.getAllCategories();

        assertEquals(2, results.size());
        assertEquals("Literature", results.get(0).name());
        assertEquals("Science", results.get(1).name());
        assertNull(results.get(0).description());
        assertFalse(results.get(1).active());

        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void getAllCategoriesReturnsEmptyListWhenRepositoryIsEmpty() {
        when(categoryRepositoryPort.findAll())
                .thenReturn(List.of());

        List<CategoryResult> results =
                service.getAllCategories();

        assertTrue(results.isEmpty());
        verifyNoInteractions(categoryUsagePort);
    }

    @Test
    void deleteCategoryDeletesExistingUnusedCategory() {
        Long categoryId = 1L;
        Category category = restoredCategory(
                categoryId,
                "Literature",
                null,
                true
        );

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryUsagePort.isCategoryUsedByAnyBook(categoryId))
                .thenReturn(false);

        service.deleteCategory(categoryId);

        verify(categoryUsagePort)
                .isCategoryUsedByAnyBook(categoryId);

        verify(categoryRepositoryPort)
                .delete(category);
    }

    @Test
    void deleteCategoryRejectsCategoryUsedByBook() {
        Long categoryId = 1L;
        Category category = restoredCategory(
                categoryId,
                "Literature",
                null,
                true
        );

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.of(category));

        when(categoryUsagePort.isCategoryUsedByAnyBook(categoryId))
                .thenReturn(true);

        assertThrows(
                CategoryInUseException.class,
                () -> service.deleteCategory(categoryId)
        );

        verify(categoryRepositoryPort, never())
                .delete(any(Category.class));
    }

    @Test
    void deleteCategoryRejectsNullId() {
        assertThrows(
                ValidationException.class,
                () -> service.deleteCategory(null)
        );

        verifyNoInteractions(
                categoryRepositoryPort,
                categoryUsagePort
        );
    }

    @Test
    void deleteCategoryThrowsNotFoundBeforeCheckingUsage() {
        Long categoryId = 1L;

        when(categoryRepositoryPort.findById(categoryId))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> service.deleteCategory(categoryId)
        );

        verifyNoInteractions(categoryUsagePort);

        verify(categoryRepositoryPort, never())
                .delete(any(Category.class));
    }

    private static Category restoredCategory(
            Long id,
            String name,
            String description,
            boolean active
    ) {
        LocalDateTime createdAt =
                LocalDateTime.of(2026, 8, 5, 8, 0);

        LocalDateTime updatedAt =
                LocalDateTime.of(2026, 8, 5, 8, 30);

        return Category.restore(
                id,
                name,
                description,
                active,
                createdAt,
                updatedAt
        );
    }
}
