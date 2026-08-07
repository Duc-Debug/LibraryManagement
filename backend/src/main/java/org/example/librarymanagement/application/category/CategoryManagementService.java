package org.example.librarymanagement.application.category;

import java.util.List;
import java.util.Objects;

import org.example.librarymanagement.application.category.exception.CategoryInUseException;
import org.example.librarymanagement.application.category.exception.CategoryNotFoundException;
import org.example.librarymanagement.application.category.exception.DuplicateCategoryNameException;
import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.domain.entity.Category;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.port.dtos.category.CategoryResult;
import org.example.librarymanagement.port.dtos.category.CreateCategoryCommand;
import org.example.librarymanagement.port.dtos.category.UpdateCategoryCommand;
import org.example.librarymanagement.port.inbound.category.CategoryManagementUseCase;
import org.example.librarymanagement.port.outbound.category.CategoryRepositoryPort;
import org.example.librarymanagement.port.outbound.category.CategoryUsagePort;

public class CategoryManagementService
        implements CategoryManagementUseCase {

    private final CategoryRepositoryPort categoryRepositoryPort;
    private final CategoryUsagePort categoryUsagePort;

    public CategoryManagementService(
            CategoryRepositoryPort categoryRepositoryPort,
            CategoryUsagePort categoryUsagePort
    ) {
        this.categoryRepositoryPort =
                Objects.requireNonNull(
                        categoryRepositoryPort,
                        "Category repository port must not be null"
                );
        this.categoryUsagePort =
                Objects.requireNonNull(
                        categoryUsagePort,
                        "Category usage port must not be null"
                );
    }

    @Override
    public CategoryResult createCategory(
            CreateCategoryCommand command
    ) {
        requireCreateCommand(command);

        Category category = createDomainCategory(command);

        ensureNameIsAvailable(category.getName());

        Category savedCategory =
                categoryRepositoryPort.save(category);

        return toResult(savedCategory);
    }

    @Override
    public CategoryResult updateCategory(
            UpdateCategoryCommand command
    ) {
        requireUpdateCommand(command);

        String requestedName =
                normalizeOptionalCommandName(command.name());
        String requestedDescription =
                normalizeOptionalCommandDescription(
                        command.description()
                );

        Category category = findCategory(command.categoryId());

        updateNameIfNecessary(
                category,
                requestedName
        );
        updateDescriptionIfNecessary(
                category,
                command.description(),
                requestedDescription
        );
        updateActiveStatus(category, command.active());

        Category savedCategory =
                categoryRepositoryPort.save(category);

        return toResult(savedCategory);
    }

    @Override
    public CategoryResult getCategoryById(
            Long categoryId
    ) {
        requireCategoryId(categoryId);

        return toResult(findCategory(categoryId));
    }

    @Override
    public List<CategoryResult> getAllCategories() {
        return categoryRepositoryPort.findAll()
                .stream()
                .map(CategoryManagementService::toResult)
                .toList();
    }

    @Override
    public void deleteCategory(
            Long categoryId
    ) {
        requireCategoryId(categoryId);

        Category category = findCategory(categoryId);

        ensureCategoryIsNotInUse(categoryId);

        categoryRepositoryPort.delete(category);
    }

    private void updateNameIfNecessary(
            Category category,
            String requestedName
    ) {
        if (requestedName == null) {
            return;
        }

        if (category.getName().equals(requestedName)) {
            category.rename(requestedName);
            return;
        }

        ensureNameIsAvailableForAnotherCategory(
                requestedName,
                category.getId()
        );

        category.rename(requestedName);
    }

    private void updateDescriptionIfNecessary(
            Category category,
            String originalDescription,
            String requestedDescription
    ) {
        if (originalDescription == null) {
            return;
        }

        category.updateDescription(requestedDescription);
    }

    private void updateActiveStatus(
            Category category,
            Boolean requestedActive
    ) {
        if (requestedActive == null) {
            return;
        }

        if (requestedActive) {
            category.activate();
        } else {
            category.deactivate();
        }
    }

    private void ensureNameIsAvailable(
            String name
    ) {
        if (categoryRepositoryPort.existsByName(name)) {
            throw new DuplicateCategoryNameException(name);
        }
    }

    private void ensureNameIsAvailableForAnotherCategory(
            String name,
            Long currentCategoryId
    ) {
        boolean nameAlreadyUsed =
                categoryRepositoryPort.existsByNameAndIdNot(
                        name,
                        currentCategoryId
                );

        if (nameAlreadyUsed) {
            throw new DuplicateCategoryNameException(name);
        }
    }

    private void ensureCategoryIsNotInUse(
            Long categoryId
    ) {
        if (categoryUsagePort.isCategoryUsedByAnyBook(
                categoryId
        )) {
            throw new CategoryInUseException(categoryId);
        }
    }

    private Category findCategory(
            Long categoryId
    ) {
        return categoryRepositoryPort.findById(categoryId)
                .orElseThrow(
                        () -> new CategoryNotFoundException(
                                categoryId
                        )
                );
    }

    private static void requireCreateCommand(
            CreateCategoryCommand command
    ) {
        if (command == null) {
            throw new ValidationException(
                    "Create category command must not be null"
            );
        }
    }

    private static void requireUpdateCommand(
            UpdateCategoryCommand command
    ) {
        if (command == null) {
            throw new ValidationException(
                    "Update category command must not be null"
            );
        }

        requireCategoryId(command.categoryId());

        if (command.name() == null
                && command.description() == null
                && command.active() == null) {
            throw new ValidationException(
                    "At least one category field must be provided"
            );
        }
    }

    private static void requireCategoryId(
            Long categoryId
    ) {
        if (categoryId == null) {
            throw new ValidationException(
                    "Category id must not be null"
            );
        }

        if (categoryId <= 0) {
            throw new ValidationException(
                    "Category id must be a positive number"
            );
        }
    }

    private static Category createDomainCategory(
            CreateCategoryCommand command
    ) {
        try {
            return Category.create(
                    command.name(),
                    command.description()
            );
        } catch (DomainException exception) {
            throw toValidationException(exception);
        }
    }

    private static String normalizeOptionalCommandName(
            String name
    ) {
        if (name == null) {
            return null;
        }

        try {
            return Category.normalizeName(name);
        } catch (DomainException exception) {
            throw toValidationException(exception);
        }
    }

    private static String normalizeOptionalCommandDescription(
            String description
    ) {
        if (description == null) {
            return null;
        }

        try {
            return Category.normalizeDescription(description);
        } catch (DomainException exception) {
            throw toValidationException(exception);
        }
    }

    private static ValidationException toValidationException(
            DomainException exception
    ) {
        return new ValidationException(
                exception.getMessage(),
                exception
        );
    }

    private static CategoryResult toResult(
            Category category
    ) {
        Objects.requireNonNull(
                category,
                "Category to map must not be null"
        );

        return new CategoryResult(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isActive(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
