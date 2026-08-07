package org.example.librarymanagement.infrastructure.web.category;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.infrastructure.web.category.dto.CategoryResponse;
import org.example.librarymanagement.infrastructure.web.category.dto.CreateCategoryRequest;
import org.example.librarymanagement.infrastructure.web.category.dto.UpdateCategoryRequest;
import org.example.librarymanagement.port.dtos.category.CategoryResult;
import org.example.librarymanagement.port.inbound.category.CategoryManagementUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryManagementUseCase categoryManagementUseCase;
    private final CategoryWebMapper mapper;

    public CategoryController(
            CategoryManagementUseCase categoryManagementUseCase,
            CategoryWebMapper mapper
    ) {
        this.categoryManagementUseCase =
                Objects.requireNonNull(
                        categoryManagementUseCase,
                        "Category management use case must not be null"
                );
        this.mapper =
                Objects.requireNonNull(
                        mapper,
                        "Category web mapper must not be null"
                );
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CreateCategoryRequest request
    ) {
        CategoryResult result =
                categoryManagementUseCase.createCategory(
                        mapper.toCreateCommand(request)
                );

        CategoryResponse response =
                mapper.toResponse(result);

        URI location = URI.create(
                "/api/categories/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody UpdateCategoryRequest request
    ) {
        CategoryResult result =
                categoryManagementUseCase.updateCategory(
                        mapper.toUpdateCommand(
                                categoryId,
                                request
                        )
                );

        return ResponseEntity.ok(
                mapper.toResponse(result)
        );
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long categoryId
    ) {
        CategoryResult result =
                categoryManagementUseCase
                        .getCategoryById(categoryId);

        return ResponseEntity.ok(
                mapper.toResponse(result)
        );
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>>
    getAllCategories() {
        List<CategoryResponse> responses =
                categoryManagementUseCase
                        .getAllCategories()
                        .stream()
                        .map(mapper::toResponse)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long categoryId
    ) {
        categoryManagementUseCase
                .deleteCategory(categoryId);

        return ResponseEntity.noContent().build();
    }
}
