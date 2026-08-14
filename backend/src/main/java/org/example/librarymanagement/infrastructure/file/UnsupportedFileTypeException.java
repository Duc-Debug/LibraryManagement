package org.example.librarymanagement.infrastructure.file;

public class UnsupportedFileTypeException extends FileStorageException {
    public UnsupportedFileTypeException(String message) {
        super(message);
    }
}
