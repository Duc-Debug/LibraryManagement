package org.example.librarymanagement.infrastructure.web.book;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;


public record CreateBookRequest(

        @NotBlank(message = "Tên sách không được trống")
        String title,


        @NotBlank(message = "Tác giả không được trống")
        String author,


        @NotBlank(message = "ISBN không được trống")
        String isbn,


        @NotNull(message = "Thể loại không được trống")
        UUID categoryId,


        @NotNull(message = "Số lượng không được trống")
        Integer totalQuantity,


        String coverImageUrl,


        String description,


        String publisher,


        Integer publishedYear,


        String shelfLocation

) {}