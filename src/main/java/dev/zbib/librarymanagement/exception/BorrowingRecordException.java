package dev.zbib.librarymanagement.exception;


import org.springframework.http.HttpStatus;

public class BorrowingRecordException {

    public static class BorrowingRecordNotFound extends AppException {
        public BorrowingRecordNotFound() {
            super("Borrowing record not found",
                    HttpStatus.NOT_FOUND);
        }
    }
}
