package com.ing.mortgage.exception;

import jakarta.validation.ConstraintViolationException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application.
 * <p>
 * Handles and maps various exceptions to appropriate HTTP responses, providing consistent error responses for clients.
 * </p>
 *
 * <ul>
 *   <li>{@link BadRequestException} - Returns 400 Bad Request with error details.</li>
 *   <li>{@link ConstraintViolationException} and {@link MethodArgumentNotValidException} -
 *   Returns 400 Bad Request with validation error details.</li>
 *   <li>{@link Exception} - Returns 500 Internal Server Error for unhandled exceptions.</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("bad_request", ex.getMessage()));
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidation(Exception ex) {
        String message = "Validation failed";

        if (ex instanceof ConstraintViolationException cve) {
            message = cve.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .findFirst()
                .orElse(message);
        } else if (ex instanceof MethodArgumentNotValidException manve) {
            message = manve.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .findFirst()
                .orElse(message);
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("validation_error", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("internal_server_error", "An unexpected error occurred"));
    }
}
