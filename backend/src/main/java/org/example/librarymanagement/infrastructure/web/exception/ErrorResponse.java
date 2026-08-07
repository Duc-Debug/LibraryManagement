package org.example.librarymanagement.infrastructure.web.exception;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(code, message, Instant.now());
    }
}
