package org.example.librarymanagement.infrastructure.web.exception;

import org.example.librarymanagement.application.auth.InvalidCredentialsException;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.infrastructure.web.auth.InvalidAuthorizationHeaderException;
import org.example.librarymanagement.port.outbound.auth.token.InvalidAccessTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Trả về thông tin chi tiết các trường bị lỗi validation (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String detailMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("VALIDATION_ERROR", detailMessage.isEmpty() ? "Invalid request" : detailMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson() {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("MALFORMED_JSON", "Malformed request body"));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("INVALID_CREDENTIALS", exception.getMessage()));
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccessToken(
            InvalidAccessTokenException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("INVALID_TOKEN", exception.getMessage()));
    }

    @ExceptionHandler(org.example.librarymanagement.domain.exceptions.UnauthenticatedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticated(
            org.example.librarymanagement.domain.exceptions.UnauthenticatedException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("UNAUTHENTICATED", exception.getMessage()));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("DOMAIN_ERROR", exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("BAD_REQUEST", exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        // Nên log lỗi ra console hoặc file để debug
        ex.printStackTrace(); 
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "Unexpected server error"));
    }
}