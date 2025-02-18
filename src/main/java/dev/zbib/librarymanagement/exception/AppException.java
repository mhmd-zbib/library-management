package dev.zbib.librarymanagement.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AppException extends RuntimeException {
    private final HttpStatus status;
    private final String message;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.message = message;
        this.status = status;
    }
}