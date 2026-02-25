package es.leinadfonfria.eyteacher.infrastructure.config;

import es.leinadfonfria.eyteacher.domain.errors.AuthException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for the application.
 */
@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * Handles authentication-related exceptions.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        log.error("Auth error: {}", ex.getMessage());
        return ResponseEntity
                .status(ex.getCode())
                .body(new ErrorResponseException(HttpStatusCode.valueOf(ex.getCode()), ex));
    }

    /**
     * Handles generic exceptions by returning a 500 Internal Server Error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(500)
                .body(new ErrorResponseException(HttpStatus.INTERNAL_SERVER_ERROR, ex));
    }
}
