package org.example.librarymanagement.application.category.exceptions;

import org.example.librarymanagement.application.shared.exception.ApplicationException;

public class CategoryInUseException
        extends ApplicationException {

    public CategoryInUseException(
            Long categoryId
    ) {
        super(
                "Category cannot be deleted because "
                        + "it is assigned to one or more books: "
                        + categoryId
        );
    }
}