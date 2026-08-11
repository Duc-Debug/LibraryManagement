package org.example.librarymanagement.infrastructure.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.example.librarymanagement.port.outbound.file.FileStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService implements FileStoragePort {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

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

    @Override
    public String storeBookImage(InputStream inputStream, String originalFilename, long size) {
        if (inputStream == null || size <= 0) {
            throw new RuntimeException("File ảnh không được để trống");
        }

        if (size > maxFileSize) {
            throw new RuntimeException("Kích thước file vượt quá giới hạn cho phép.");
        }

        byte[] bytes;
        try {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc nội dung file để tải lên", e);
        }

        if (bytes.length < 12) {
            throw new RuntimeException("File không đủ dung lượng tối thiểu để xác thực định dạng ảnh.");
        }

        // 1. Kiểm tra Magic Bytes / File Signature từ nội dung nhị phân thực tế
        byte[] header = Arrays.copyOfRange(bytes, 0, 12);
        String detectedMimeType = detectRealMimeType(header);
        if (detectedMimeType == null || !ALLOWED_CONTENT_TYPES.contains(detectedMimeType)) {
            throw new RuntimeException("Định dạng file không thực sự là ảnh hợp lệ (chỉ chấp nhận JPEG, PNG, WEBP qua Magic Bytes).");
        }

        // 2. Decode kiểm tra cấu trúc dữ liệu ảnh thực tế bằng ImageIO (đối với JPEG & PNG)
        if ("image/jpeg".equals(detectedMimeType) || "image/png".equals(detectedMimeType)) {
            try (InputStream is = new ByteArrayInputStream(bytes)) {
                BufferedImage image = ImageIO.read(is);
                if (image == null) {
                    throw new RuntimeException("Nội dung file bị lỗi hoặc không thể giải mã thành ảnh hợp lệ.");
                }
            } catch (IOException e) {
                throw new RuntimeException("Lỗi trong quá trình giải mã dữ liệu ảnh", e);
            }
        }

        String extension = switch (detectedMimeType) {
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

            try (InputStream is = new ByteArrayInputStream(bytes)) {
                Files.copy(
                        is,
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
     * Xác thực định dạng file thực tế qua Magic Bytes / File Signature
     */
    private String detectRealMimeType(byte[] header) {
        if (header == null || header.length < 12) {
            return null;
        }

        // JPEG: FF D8 FF
        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }

        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if ((header[0] & 0xFF) == 0x89 && (header[1] & 0xFF) == 0x50 && (header[2] & 0xFF) == 0x4E && (header[3] & 0xFF) == 0x47
                && (header[4] & 0xFF) == 0x0D && (header[5] & 0xFF) == 0x0A && (header[6] & 0xFF) == 0x1A && (header[7] & 0xFF) == 0x0A) {
            return "image/png";
        }

        // WEBP: "RIFF" ở offset 0-3 và "WEBP" ở offset 8-11
        if ((header[0] & 0xFF) == 0x52 && (header[1] & 0xFF) == 0x49 && (header[2] & 0xFF) == 0x46 && (header[3] & 0xFF) == 0x46
                && (header[8] & 0xFF) == 0x57 && (header[9] & 0xFF) == 0x45 && (header[10] & 0xFF) == 0x42 && (header[11] & 0xFF) == 0x50) {
            return "image/webp";
        }

        return null;
    }

    /**
     * Xóa file vật lý khi có lỗi xảy ra (Compensation action)
     */
    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        Path targetLocation = null;
        try {
            String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            targetLocation = uploadPath.resolve(filename).normalize();

            if (!targetLocation.startsWith(uploadPath)) {
                throw new SecurityException("Đường dẫn file không hợp lệ!");
            }

            Files.deleteIfExists(targetLocation);

        } catch (Exception e) {
            log.error("Không thể xóa file mồ côi (Rollback storage thất bại). File URL: {}, Path: {}", fileUrl, targetLocation, e);
        }
    }
}