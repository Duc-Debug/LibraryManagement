package org.example.librarymanagement.application.category.exceptions;

import org.example.librarymanagement.application.shared.exception.ApplicationException;

public class CategoryNotFoundException
        extends ApplicationException {

    public CategoryNotFoundException(
            Long categoryId
    ) {
        super(
                "Category not found with id: "
                        + categoryId
        );
    }
}