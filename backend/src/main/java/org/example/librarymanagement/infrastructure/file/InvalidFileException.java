package org.example.librarymanagement.infrastructure.file;

public class InvalidFileException extends FileStorageException {
    public InvalidFileException(String message) {
        super(message);
    }
}
