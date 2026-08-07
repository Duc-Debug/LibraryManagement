package org.example.librarymanagement.infrastructure.file;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;
    private final long maxFileSize;
    
    // Whitelist các định dạng ảnh an toàn
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg", 
            "image/png", 
            "image/webp"
    );

    public FileStorageService(
            @Value("${app.storage.upload-dir:uploads/books}") String uploadDir,
            @Value("${app.storage.max-file-size:5242880}") long maxFileSize
    ) {
        // Normalize thành đường dẫn tuyệt đối để tránh sai sót
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.maxFileSize = maxFileSize;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Không thể tạo thư mục upload", e);
        }
    }

    public String storeBookImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File ảnh không được để trống");
        }

        if (file.getSize() > maxFileSize) {
            throw new RuntimeException("Kích thước file vượt quá giới hạn cho phép.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new RuntimeException("Chỉ chấp nhận file ảnh định dạng hợp lệ (JPEG, PNG, WEBP).");
        }

        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new RuntimeException("Định dạng không được hỗ trợ"); 
        };

        String filename = UUID.randomUUID() + extension;

        try {
            Path targetLocation = uploadPath.resolve(filename).normalize();

            if (!targetLocation.startsWith(uploadPath)) {
                throw new SecurityException("Phát hiện cố gắng lưu file ngoài thư mục cho phép!");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(
                        inputStream,
                        targetLocation,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

            return "/uploads/books/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại", e);
        }
    }

    /**
     * Xóa file vật lý khi có lỗi xảy ra (Compensation action)
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            // Lấy tên file từ URL (ví dụ: "/uploads/books/uuid.jpg" -> "uuid.jpg")
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            
            // Xác định đường dẫn tuyệt đối
            Path targetLocation = uploadPath.resolve(filename).normalize();

            // Kiểm tra an toàn (Path Traversal Check)
            if (!targetLocation.startsWith(uploadPath)) {
                throw new SecurityException("Đường dẫn file không hợp lệ!");
            }

            // Thực hiện xóa file nếu nó tồn tại trên ổ cứng
            Files.deleteIfExists(targetLocation);
            
        } catch (IOException e) {
            // Chỉ log ra lỗi thay vì ném tiếp để không làm gián đoạn luồng throw exception chính
            System.err.println("Không thể xóa file mồ côi (Rollback storage thất bại): " + e.getMessage());
        }
    }
}