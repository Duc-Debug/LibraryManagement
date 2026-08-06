package org.example.librarymanagement.port.outbound.category;

public interface CategoryUsagePort {

    boolean isCategoryUsedByAnyBook(
            Long categoryId
    );
}