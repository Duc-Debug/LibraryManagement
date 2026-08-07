package org.example.librarymanagement.domain.exceptions;

public class AccessDeniedException extends DomainException {
    public AccessDeniedException(String message) {
        super(message);
    }

    public static AccessDeniedException defaultMessage() {
        return new AccessDeniedException("Truy cập bị từ chối: Bạn không có quyền thực hiện hành động này.");
    }
}
