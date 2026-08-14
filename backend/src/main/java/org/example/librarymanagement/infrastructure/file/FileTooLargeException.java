package org.example.librarymanagement.infrastructure.file;

public class FileTooLargeException extends FileStorageException {
    public FileTooLargeException(String message) {
        super(message);
    }
}
