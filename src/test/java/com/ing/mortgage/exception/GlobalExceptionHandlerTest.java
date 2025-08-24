package com.ing.mortgage.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Collections;
import java.util.Set;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleBadRequest_receivedException_returnErrorResponse() {
        BadRequestException ex = new BadRequestException("bad input");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleBadRequest(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("bad_request", response.getBody().key());
        assertEquals("bad input", response.getBody().message());
    }

    @Test
    void handleValidation_constraintViolation_returnErrorResponse() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);

        Path path = mock(Path.class);

        when(path.toString()).thenReturn("field");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations = Collections.singleton(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("validation_error", response.getBody().key());
        assertTrue(response.getBody().message().contains("field: must not be null"));
    }

    @Test
    void handleValidation_invalidArgument_returnErrorResponse() {
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("object", "field", "must not be blank");

        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(fieldError));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidation(ex);

        assertEquals(400, response.getStatusCode().value());
        assertEquals("validation_error", response.getBody().key());
        assertTrue(response.getBody().message().contains("field: must not be blank"));
    }

    @Test
    void handleGeneric_receivedException_returnErrorResponse() {
        Exception ex = new Exception("something went wrong");
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGeneric(ex);

        assertEquals(500, response.getStatusCode().value());
        assertEquals("internal_server_error", response.getBody().key());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }
}