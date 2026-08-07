package org.example.librarymanagement.infrastructure.web.librarians.dto;

import jakarta.validation.constraints.Min;

public record ReplenishStockRequest(
        @Min(value = 1, message = "Số lượng nhập kho phải lớn hơn hoặc bằng 1")
        int quantityToAdd
) {}
