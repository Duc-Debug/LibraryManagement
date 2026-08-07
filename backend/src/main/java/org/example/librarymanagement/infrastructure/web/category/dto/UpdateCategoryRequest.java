package org.example.librarymanagement.infrastructure.web.category.dto;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @Size(
                max = 100,
                message = "Category name must not exceed 100 characters"
        )
        String name,

        @Size(
                max = 500,
                message = "Category description must not exceed 500 characters"
        )
        String description,

        Boolean active
) {
}