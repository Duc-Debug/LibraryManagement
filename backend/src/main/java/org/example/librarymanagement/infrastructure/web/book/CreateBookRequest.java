package org.example.librarymanagement.infrastructure.web.book;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateBookRequest(

        @NotBlank(message = "Tên sách không được trống")
        @Size(max = 255, message = "Tên sách không được vượt quá 255 ký tự")
        String title,

        @NotBlank(message = "Tác giả không được trống")
        @Size(max = 150, message = "Tác giả không được vượt quá 150 ký tự")
        String author,

        @NotBlank(message = "ISBN không được trống")
        @Size(max = 20, message = "ISBN không được vượt quá 20 ký tự")
        String isbn,

        // Yêu cầu 1: Đổi kiểu từ UUID sang Long để đồng bộ với Database, Domain và Command
        @NotNull(message = "Thể loại không được trống")
        Long categoryId,

        // Yêu cầu 2: Bổ sung @Positive để đảm bảo số lượng phải lớn hơn 0
        @NotNull(message = "Số lượng không được trống")
        @Positive(message = "Số lượng sách phải lớn hơn 0")
        Integer totalQuantity,

        // Yêu cầu 5: Đã xóa trường `String coverImageUrl` 
        // Lý do: Giá trị này do hệ thống tự sinh ra sau khi upload file (MultipartFile)
        // qua FileStorageService. Client không được phép tự truyền URL này lên.

        String description,

        // Yêu cầu 3: Đồng bộ giới hạn độ dài với cấu hình Database
        @Size(max = 150, message = "Tên nhà xuất bản không được vượt quá 150 ký tự")
        String publisher,

        // Yêu cầu 4: Thêm giới hạn cơ bản cho năm xuất bản. 
        // (Ràng buộc năm <= năm hiện tại đã được xử lý ở tầng Domain Entity)
        @Min(value = 1000, message = "Năm xuất bản không hợp lệ (phải >= 1000)")
        Integer publishedYear,

        // Yêu cầu 3: Đồng bộ giới hạn độ dài
        @Size(max = 50, message = "Vị trí kệ không được vượt quá 50 ký tự")
        String shelfLocation

) {}