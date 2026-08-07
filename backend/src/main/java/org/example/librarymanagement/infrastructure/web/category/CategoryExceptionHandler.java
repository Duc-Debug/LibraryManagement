package org.example.librarymanagement.infrastructure.web.category;

import org.example.librarymanagement.application.category.exceptions.CategoryInUseException;
import org.example.librarymanagement.application.category.exceptions.CategoryNotFoundException;
import org.example.librarymanagement.application.category.exceptions.DuplicateCategoryNameException;
import org.example.librarymanagement.application.shared.ValidationException;
import org.example.librarymanagement.infrastructure.web.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice(assignableTypes = CategoryController.class)
public class CategoryExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        "VALIDATION_ERROR",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        "VALIDATION_ERROR",
                        "Category id must be a valid number"
                ));
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            CategoryNotFoundException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(
                        "CATEGORY_NOT_FOUND",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(DuplicateCategoryNameException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateName(
            DuplicateCategoryNameException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        "DUPLICATE_CATEGORY_NAME",
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(CategoryInUseException.class)
    public ResponseEntity<ErrorResponse> handleCategoryInUse(
            CategoryInUseException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(
                        "CATEGORY_IN_USE",
                        exception.getMessage()
                ));
    }
}
