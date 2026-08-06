package org.example.librarymanagement.infrastructure.web.category;

import java.util.Objects;

import org.example.librarymanagement.infrastructure.web.category.dto.CategoryResponse;
import org.example.librarymanagement.infrastructure.web.category.dto.CreateCategoryRequest;
import org.example.librarymanagement.infrastructure.web.category.dto.UpdateCategoryRequest;
import org.example.librarymanagement.port.inbound.category.CategoryResult;
import org.example.librarymanagement.port.inbound.category.CreateCategoryCommand;
import org.example.librarymanagement.port.inbound.category.UpdateCategoryCommand;
import org.springframework.stereotype.Component;

@Component
public class CategoryWebMapper {

    public CreateCategoryCommand toCreateCommand(
            CreateCategoryRequest request
    ) {
        Objects.requireNonNull(
                request,
                "Create category request must not be null"
        );

        return new CreateCategoryCommand(
                request.name(),
                request.description()
        );
    }

    public UpdateCategoryCommand toUpdateCommand(
            Long categoryId,
            UpdateCategoryRequest request
    ) {
        Objects.requireNonNull(
                categoryId,
                "Category id must not be null"
        );

        Objects.requireNonNull(
                request,
                "Update category request must not be null"
        );

        return new UpdateCategoryCommand(
                categoryId,
                request.name(),
                request.description(),
                request.active()
        );
    }

    public CategoryResponse toResponse(
            CategoryResult result
    ) {
        Objects.requireNonNull(
                result,
                "Category result must not be null"
        );

        return new CategoryResponse(
                result.id(),
                result.name(),
                result.description(),
                result.active(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}