package dev.zbib.librarymanagement.exception;

import org.springframework.http.HttpStatus;

public class PatronException {

    public static class PatronNotFound extends AppException {
        public PatronNotFound() {
            super("Patron not found",
                    HttpStatus.NOT_FOUND);
        }
    }
}

