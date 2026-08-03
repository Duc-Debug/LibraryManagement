package org.example.librarymanagement.port.outbound.auth.token;


public class ExpiredAccessTokenException
        extends InvalidAccessTokenException {

    public ExpiredAccessTokenException(String message) {
        super(message);
    }

    public ExpiredAccessTokenException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}