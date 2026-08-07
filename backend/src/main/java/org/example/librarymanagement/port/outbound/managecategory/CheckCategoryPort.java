package org.example.librarymanagement.port.outbound.managecategory;

public interface CheckCategoryPort {
    boolean existsById(Long categoryId);
}