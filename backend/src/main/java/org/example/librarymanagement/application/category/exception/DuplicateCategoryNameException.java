package org.example.librarymanagement.application.category.exception;

import org.example.librarymanagement.application.shared.exception.ApplicationException;

public class DuplicateCategoryNameException extends ApplicationException {

    public DuplicateCategoryNameException(String categoryName) {
        super("Category name already exists: " + categoryName);
    }
}
