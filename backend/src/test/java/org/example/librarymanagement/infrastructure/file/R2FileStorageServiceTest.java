package org.example.librarymanagement.infrastructure.file;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.example.librarymanagement.infrastructure.config.R2Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@ExtendWith(MockitoExtension.class)
class R2FileStorageServiceTest {

    @Mock
    private S3Client s3Client;

    private R2Properties properties;
    private R2FileStorageService fileStorageService;
    private final long maxFileSize = 100; // 100 bytes limit for easy testing

    @BeforeEach
    void setUp() {
        properties = new R2Properties();
        properties.setEndpoint("https://example.r2.cloudflarestorage.com");
        properties.setAccessKey("access-key");
        properties.setSecretKey("secret-key");
        properties.setBucketName("library-bucket");
        properties.setPublicUrl("https://example.r2.dev/");

        fileStorageService = new R2FileStorageService(s3Client, properties);
        org.springframework.test.util.ReflectionTestUtils.setField(fileStorageService, "maxFileSize", maxFileSize);
    }

    // ==================== HELPERS ====================

    private byte[] createJpegHeader() {
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0xFF;
        bytes[1] = (byte) 0xD8;
        bytes[2] = (byte) 0xFF;
        return bytes;
    }

    private byte[] createPngHeader() {
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x89;
        bytes[1] = (byte) 0x50;
        bytes[2] = (byte) 0x4E;
        bytes[3] = (byte) 0x47;
        bytes[4] = (byte) 0x0D;
        bytes[5] = (byte) 0x0A;
        bytes[6] = (byte) 0x1A;
        bytes[7] = (byte) 0x0A;
        return bytes;
    }

    private byte[] createWebpHeader() {
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x52; // R
        bytes[1] = (byte) 0x49; // I
        bytes[2] = (byte) 0x46; // F
        bytes[3] = (byte) 0x46; // F
        bytes[8] = (byte) 0x57; // W
        bytes[9] = (byte) 0x45; // E
        bytes[10] = (byte) 0x42; // B
        bytes[11] = (byte) 0x50; // P
        return bytes;
    }

    private byte[] createExeHeader() {
        byte[] bytes = new byte[12];
        bytes[0] = (byte) 0x4D; // M
        bytes[1] = (byte) 0x5A; // Z
        return bytes;
    }

    // ==================== TESTS ====================

    @Test
    @DisplayName("storeBookImage: JPEG hợp lệ -> upload thành công")
    void storeBookImage_Success_Jpeg() {
        byte[] data = createJpegHeader();
        InputStream is = new ByteArrayInputStream(data);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        String result = fileStorageService.storeBookImage(is, "test.jpg", data.length);

        assertNotNull(result);
        assertTrue(result.startsWith("https://example.r2.dev/books/"));
        assertTrue(result.endsWith(".jpg"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("storeBookImage: PNG hợp lệ -> upload thành công")
    void storeBookImage_Success_Png() {
        byte[] data = createPngHeader();
        InputStream is = new ByteArrayInputStream(data);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        String result = fileStorageService.storeBookImage(is, "test.png", data.length);

        assertNotNull(result);
        assertTrue(result.startsWith("https://example.r2.dev/books/"));
        assertTrue(result.endsWith(".png"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("storeBookImage: WEBP hợp lệ -> upload thành công")
    void storeBookImage_Success_Webp() {
        byte[] data = createWebpHeader();
        InputStream is = new ByteArrayInputStream(data);
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class))).thenReturn(null);

        String result = fileStorageService.storeBookImage(is, "test.webp", data.length);

        assertNotNull(result);
        assertTrue(result.startsWith("https://example.r2.dev/books/"));
        assertTrue(result.endsWith(".webp"));
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("storeBookImage: File không đúng magic bytes (văn bản thường) -> reject")
    void storeBookImage_ThrowsUnsupportedFileTypeException_WhenMagicBytesMismatch() {
        byte[] data = "Hello World Plain Text".getBytes();
        InputStream is = new ByteArrayInputStream(data);

        UnsupportedFileTypeException exception = assertThrows(
                UnsupportedFileTypeException.class,
                () -> fileStorageService.storeBookImage(is, "test.jpg", data.length)
        );

        assertTrue(exception.getMessage().contains("chỉ chấp nhận JPEG, PNG, WEBP"));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("storeBookImage: Executable (EXE) đổi tên thành .jpg -> reject")
    void storeBookImage_ThrowsUnsupportedFileTypeException_WhenExecutableRenamed() {
        byte[] data = createExeHeader();
        InputStream is = new ByteArrayInputStream(data);

        UnsupportedFileTypeException exception = assertThrows(
                UnsupportedFileTypeException.class,
                () -> fileStorageService.storeBookImage(is, "virus.jpg", data.length)
        );

        assertTrue(exception.getMessage().contains("chỉ chấp nhận JPEG, PNG, WEBP"));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("storeBookImage: File rỗng -> reject")
    void storeBookImage_ThrowsInvalidFileException_WhenFileIsEmpty() {
        InputStream is = new ByteArrayInputStream(new byte[0]);

        InvalidFileException exception = assertThrows(
                InvalidFileException.class,
                () -> fileStorageService.storeBookImage(is, "empty.png", 0)
        );

        assertEquals("File ảnh không được để trống", exception.getMessage());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("storeBookImage: Kích thước file vượt giới hạn cho phép -> reject")
    void storeBookImage_ThrowsFileTooLargeException_WhenFileSizeExceedsLimit() {
        byte[] data = createJpegHeader();
        InputStream is = new ByteArrayInputStream(data);

        // Kích thước truyền vào (150) lớn hơn maxFileSize (100)
        FileTooLargeException exception = assertThrows(
                FileTooLargeException.class,
                () -> fileStorageService.storeBookImage(is, "large.jpg", 150)
        );

        assertEquals("Kích thước file vượt quá giới hạn cho phép.", exception.getMessage());
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("storeBookImage: Định dạng file (PDF) không hỗ trợ -> reject")
    void storeBookImage_ThrowsUnsupportedFileTypeException_WhenMimeTypeNotSupported() {
        byte[] pdfHeader = new byte[12];
        pdfHeader[0] = 0x25; // %
        pdfHeader[1] = 0x50; // P
        pdfHeader[2] = 0x44; // D
        pdfHeader[3] = 0x46; // F
        InputStream is = new ByteArrayInputStream(pdfHeader);

        UnsupportedFileTypeException exception = assertThrows(
                UnsupportedFileTypeException.class,
                () -> fileStorageService.storeBookImage(is, "document.pdf", pdfHeader.length)
        );

        assertTrue(exception.getMessage().contains("chỉ chấp nhận JPEG, PNG, WEBP"));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("deleteFile: Xóa tệp tin thành công gọi s3Client.deleteObject")
    void deleteFile_Success() {
        String fileUrl = "https://example.r2.dev/books/test-uuid.jpg";
        when(s3Client.deleteObject(any(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.class))).thenReturn(null);

        fileStorageService.deleteFile(fileUrl);

        verify(s3Client).deleteObject(any(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("deleteFile: Thất bại khi gọi S3 -> ném FileStorageException")
    void deleteFile_ThrowsFileStorageException_WhenS3Fails() {
        String fileUrl = "https://example.r2.dev/books/test-uuid.jpg";
        when(s3Client.deleteObject(any(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.class)))
                .thenThrow(new RuntimeException("S3 connection timeout"));

        assertThrows(
                FileStorageException.class,
                () -> fileStorageService.deleteFile(fileUrl)
        );

        verify(s3Client).deleteObject(any(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("deleteFile: Key ngoài phạm vi books/ -> ném InvalidFileException")
    void deleteFile_ThrowsInvalidFileException_WhenKeyNotInBooksNamespace() {
        String fileUrl = "https://example.r2.dev/other/test-uuid.jpg";

        assertThrows(
                InvalidFileException.class,
                () -> fileStorageService.deleteFile(fileUrl)
        );

        verify(s3Client, never()).deleteObject(any(software.amazon.awssdk.services.s3.model.DeleteObjectRequest.class));
    }
}
