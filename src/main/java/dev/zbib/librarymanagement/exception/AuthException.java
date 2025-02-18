package dev.zbib.librarymanagement.exception;

import org.springframework.http.HttpStatus;

public class AuthException {

    public static class EmailAlreadyExists extends AppException {
        public EmailAlreadyExists() {
            super("Email already exists",
                    HttpStatus.CONFLICT);
        }
    }
}
