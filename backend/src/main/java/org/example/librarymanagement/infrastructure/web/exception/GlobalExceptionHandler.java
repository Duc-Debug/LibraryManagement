package org.example.librarymanagement.infrastructure.web.exception;

import java.util.stream.Collectors;

import org.example.librarymanagement.application.auth.InvalidCredentialsException;
import org.example.librarymanagement.domain.exceptions.DomainException;
import org.example.librarymanagement.domain.exceptions.ReaderAccessDeniedException;
import org.example.librarymanagement.domain.exceptions.ReaderAlreadyExistsException;
import org.example.librarymanagement.domain.exceptions.ReaderHasActiveBorrowException;
import org.example.librarymanagement.domain.exceptions.ReaderNotFoundException;
import org.example.librarymanagement.domain.exceptions.UnauthenticatedException;
import org.example.librarymanagement.infrastructure.web.auth.InvalidAuthorizationHeaderException;
import org.example.librarymanagement.port.outbound.auth.token.InvalidAccessTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String detailMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(
                        "VALIDATION_ERROR",
                        detailMessage.isEmpty() ? "Invalid request" : detailMessage
                ));
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

    @ExceptionHandler(InvalidAuthorizationHeaderException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAuthorizationHeader(
            InvalidAuthorizationHeaderException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of("INVALID_AUTHORIZATION_HEADER", exception.getMessage()));
    }

    @ExceptionHandler(InvalidAccessTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccessToken(
            InvalidAccessTokenException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of("INVALID_TOKEN", exception.getMessage()));
    }

   @ExceptionHandler(UnauthenticatedException.class)
public ResponseEntity<ErrorResponse> handleUnauthenticated(
        UnauthenticatedException exception
) {
    return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse.of(
                    "UNAUTHENTICATED",
                    exception.getMessage()
            ));
}
@ExceptionHandler(ReaderNotFoundException.class)
public ResponseEntity<ErrorResponse> handleReaderNotFound(
        ReaderNotFoundException exception
) {
    return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.of(
                    "READER_NOT_FOUND",
                    exception.getMessage()
            ));
}

@ExceptionHandler(ReaderAccessDeniedException.class)
public ResponseEntity<ErrorResponse> handleReaderAccessDenied(
        ReaderAccessDeniedException exception
) {
    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse.of(
                    "READER_ACCESS_DENIED",
                    exception.getMessage()
            ));
}

@ExceptionHandler(ReaderAlreadyExistsException.class)
public ResponseEntity<ErrorResponse> handleReaderAlreadyExists(
        ReaderAlreadyExistsException exception
) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.of(
                    "READER_ALREADY_EXISTS",
                    exception.getMessage()
            ));
}
@ExceptionHandler(ReaderHasActiveBorrowException.class)
public ResponseEntity<ErrorResponse> handleReaderHasActiveBorrow(
        ReaderHasActiveBorrowException exception
) {
    return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.of(
                    "READER_HAS_ACTIVE_BORROW",
                    exception.getMessage()
            ));
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
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        log.error("Unexpected exception", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of("INTERNAL_ERROR", "Unexpected server error"));
    }
}
