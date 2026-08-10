package org.example.librarymanagement.port.outbound.file;

import java.io.InputStream;

public interface 

 {
    String storeBookImage(InputStream inputStream, String originalFilename, long size);
    void deleteFile(String fileUrl);
}
