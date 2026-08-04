package org.example.librarymanagement.infrastructure.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {


    private final Path uploadPath =
            Paths.get("uploads/books");


    public FileStorageService() {

        try {

            Files.createDirectories(uploadPath);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Không thể tạo thư mục upload",
                    e
            );

        }

    }



    public String storeBookImage(MultipartFile file) {


        if (file == null || file.isEmpty()) {

            throw new RuntimeException(
                    "File ảnh không được để trống"
            );

        }


        String originalFilename =
                file.getOriginalFilename();


        String extension = "";


        if(originalFilename != null 
                && originalFilename.contains(".")) {

            extension =
                    originalFilename.substring(
                            originalFilename.lastIndexOf(".")
                    );

        }



        String filename =
                UUID.randomUUID()
                        + extension;



        try {


            Path target =
                    uploadPath.resolve(filename);



            Files.copy(
                    file.getInputStream(),
                    target,
                    StandardCopyOption.REPLACE_EXISTING
            );



            return "/uploads/books/" + filename;



        } catch (IOException e) {


            throw new RuntimeException(
                    "Upload ảnh thất bại",
                    e
            );

        }

    }

}