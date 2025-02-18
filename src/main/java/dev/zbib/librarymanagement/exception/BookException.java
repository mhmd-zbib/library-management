package dev.zbib.librarymanagement.exception;

import org.springframework.http.HttpStatus;

public class BookException {

    public static class BookNotFound extends AppException {
        public BookNotFound() {
            super("Book not found",
                    HttpStatus.NOT_FOUND);
        }
    }
}
