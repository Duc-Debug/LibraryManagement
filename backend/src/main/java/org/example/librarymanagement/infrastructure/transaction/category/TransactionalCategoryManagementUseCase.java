package org.example.librarymanagement.infrastructure.transaction.category;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.port.dtos.category.CategoryResult;
import org.example.librarymanagement.port.dtos.category.CreateCategoryCommand;
import org.example.librarymanagement.port.dtos.category.UpdateCategoryCommand;
import org.example.librarymanagement.port.inbound.category.CategoryManagementUseCase;
import org.springframework.transaction.annotation.Transactional;

public class TransactionalCategoryManagementUseCase
        implements CategoryManagementUseCase {

    private final CategoryManagementUseCase delegate;

    public TransactionalCategoryManagementUseCase(
            CategoryManagementUseCase delegate
    ) {
        this.delegate =
                Objects.requireNonNull(
                        delegate,
                        "Category management delegate must not be null"
                );
    }

    @Override
    @Transactional
    public CategoryResult createCategory(
            CreateCategoryCommand command
    ) {
        return delegate.createCategory(command);
    }

    @Override
    @Transactional
    public CategoryResult updateCategory(
            UpdateCategoryCommand command
    ) {
        return delegate.updateCategory(command);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResult getCategoryById(
            Long categoryId
    ) {
        return delegate.getCategoryById(
                categoryId
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResult> getAllCategories() {
        return delegate.getAllCategories();
    }

    @Override
    @Transactional
    public void deleteCategory(
            Long categoryId
    ) {
        delegate.deleteCategory(categoryId);
    }
}