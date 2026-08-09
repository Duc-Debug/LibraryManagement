package org.example.librarymanagement.port.inbound.category;

import java.util.List;

import org.example.librarymanagement.port.dtos.category.CategoryResult;
import org.example.librarymanagement.port.dtos.category.CreateCategoryCommand;
import org.example.librarymanagement.port.dtos.category.UpdateCategoryCommand;

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