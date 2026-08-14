package org.example.librarymanagement.port.outbound.file;

import java.io.InputStream;

import org.example.librarymanagement.infrastructure.file.FileStorageException;

public interface FileStoragePort {

    /**
     * Tải tệp tin ảnh sách lên hệ thống lưu trữ.
     *
     * @param inputStream      Luồng tệp tin
     * @param originalFilename Tên tệp gốc
     * @param size             Dung lượng tệp tin (bytes)
     * @return Đường dẫn URL của tệp tin đã lưu
     * @throws FileStorageException khi có lỗi xảy ra trong quá trình lưu tệp tin
     */
    String storeBookImage(InputStream inputStream, String originalFilename, long size);

    /**
     * Xóa tệp tin đã lưu trữ theo đường dẫn URL.
     *
     * @param fileUrl Đường dẫn URL của tệp tin cần xóa
     * @throws FileStorageException khi có lỗi xảy ra trong quá trình xóa tệp tin
     */
    void deleteFile(String fileUrl);
}

