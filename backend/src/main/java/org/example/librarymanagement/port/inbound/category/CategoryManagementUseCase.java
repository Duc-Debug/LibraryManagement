package org.example.librarymanagement.port.inbound.category;

import java.util.List;

public interface CategoryManagementUseCase {

    CategoryResult createCategory(
            CreateCategoryCommand command
    );

    CategoryResult updateCategory(
            UpdateCategoryCommand command
    );

    CategoryResult getCategoryById(
            Long categoryId
    );

    List<CategoryResult> getAllCategories();

    void deleteCategory(
            Long categoryId
    );
}