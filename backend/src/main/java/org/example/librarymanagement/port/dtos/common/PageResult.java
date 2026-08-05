package org.example.librarymanagement.port.dtos.common;

import java.util.List;

public record PageResult<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static <T> PageResult<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new PageResult<>(content, page, size, totalElements, totalPages);
    }
}
