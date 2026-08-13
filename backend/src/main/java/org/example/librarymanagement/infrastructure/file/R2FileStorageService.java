package org.example.librarymanagement.infrastructure.file;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.example.librarymanagement.port.outbound.file.FileStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@Primary
public class R2FileStorageService implements FileStoragePort {

    private static final Logger log = LoggerFactory.getLogger(R2FileStorageService.class);

    private final S3Client s3Client;
    private final String bucketName;
    private final String publicUrl;
    private final long maxFileSize;

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    public R2FileStorageService(
            S3Client s3Client,
            @Value("${cloud.r2.bucket-name:library-books}") String bucketName,
            @Value("${cloud.r2.public-url:}") String publicUrl,
            @Value("${app.storage.max-file-size:5242880}") long maxFileSize
    ) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
        this.publicUrl = (publicUrl != null && publicUrl.endsWith("/"))
                ? publicUrl.substring(0, publicUrl.length() - 1)
                : publicUrl;
        this.maxFileSize = maxFileSize;
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
            bytes = readWithLimit(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc nội dung file để tải lên", e);
        }

        if (bytes.length < 12) {
            throw new RuntimeException("File không đủ dung lượng tối thiểu để xác thực định dạng ảnh.");
        }

        // 1. Kiểm tra Magic Bytes / File Signature
        byte[] header = Arrays.copyOfRange(bytes, 0, 12);
        String detectedMimeType = detectRealMimeType(header);
        if (detectedMimeType == null || !ALLOWED_CONTENT_TYPES.contains(detectedMimeType)) {
            throw new RuntimeException("Định dạng file không thực sự là ảnh hợp lệ (chỉ chấp nhận JPEG, PNG, WEBP).");
        }

        String extension = switch (detectedMimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new RuntimeException("Định dạng không được hỗ trợ");
        };

        String filename = "books/" + UUID.randomUUID() + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filename)
                    .contentType(detectedMimeType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            // Trả về Public URL nếu có cấu hình, nếu không trả về key path
            if (publicUrl != null && !publicUrl.isBlank()) {
                return publicUrl + "/" + filename;
            }
            return "/" + filename;

        } catch (Exception e) {
            log.error("Tải file lên Cloudflare R2 thất bại", e);
            throw new RuntimeException("Upload ảnh lên R2 thất bại: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            String key = extractObjectKey(fileUrl);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Xóa file trên Cloudflare R2 thất bại: {}", fileUrl, e);
        }
    }

    private String extractObjectKey(String fileUrl) {
        if (publicUrl != null && !publicUrl.isBlank() && fileUrl.startsWith(publicUrl)) {
            String relative = fileUrl.substring(publicUrl.length());
            return relative.startsWith("/") ? relative.substring(1) : relative;
        }
        if (fileUrl.startsWith("/")) {
            return fileUrl.substring(1);
        }
        return fileUrl;
    }

    private String detectRealMimeType(byte[] header) {
        if (header == null || header.length < 12) {
            return null;
        }

        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
            return "image/jpeg";
        }

        if ((header[0] & 0xFF) == 0x89 && (header[1] & 0xFF) == 0x50 && (header[2] & 0xFF) == 0x4E && (header[3] & 0xFF) == 0x47
                && (header[4] & 0xFF) == 0x0D && (header[5] & 0xFF) == 0x0A && (header[6] & 0xFF) == 0x1A && (header[7] & 0xFF) == 0x0A) {
            return "image/png";
        }

        if ((header[0] & 0xFF) == 0x52 && (header[1] & 0xFF) == 0x49 && (header[2] & 0xFF) == 0x46 && (header[3] & 0xFF) == 0x46
                && (header[8] & 0xFF) == 0x57 && (header[9] & 0xFF) == 0x45 && (header[10] & 0xFF) == 0x42 && (header[11] & 0xFF) == 0x50) {
            return "image/webp";
        }

        return null;
    }

    private byte[] readWithLimit(InputStream inputStream) throws IOException {
        long limit = maxFileSize + 1;
        byte[] bytes = inputStream.readNBytes((int) Math.min(limit, Integer.MAX_VALUE));

        if (bytes.length > maxFileSize) {
            throw new RuntimeException("Kích thước file thực tế vượt quá giới hạn cho phép.");
        }

        return bytes;
    }
}
