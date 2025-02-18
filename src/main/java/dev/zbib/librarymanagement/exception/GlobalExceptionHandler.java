package dev.zbib.librarymanagement.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED,
                "Authentication failed: " + ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.FORBIDDEN,
                "Access denied: " + ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ValidationError> validationErrors = new ArrayList<>();
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    if (error instanceof FieldError fieldError) {
                        validationErrors.add(ValidationError.builder()
                                .field(fieldError.getField())
                                .message(fieldError.getDefaultMessage())
                                .rejectedValue(fieldError.getRejectedValue())
                                .build());
                        errors.put(fieldError.getField(),
                                fieldError.getDefaultMessage());
                    } else {
                        errors.put(error.getObjectName(),
                                error.getDefaultMessage());
                    }
                });

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed")
                .path(request.getRequestURI())
                .errors(validationErrors)
                .build();

        return ResponseEntity.badRequest()
                .body(errorResponse);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(Exception ex, HttpServletRequest request) {
        return createErrorResponse(HttpStatus.BAD_REQUEST,
                "Invalid request parameter: " + ex.getMessage(),
                request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred",
                ex);
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.",
                request.getRequestURI());
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        return new ResponseEntity<>(errorResponse,
                status);
    }
}