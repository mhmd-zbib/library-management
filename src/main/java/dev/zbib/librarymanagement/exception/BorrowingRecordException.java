package dev.zbib.librarymanagement.exception;

import org.springframework.http.HttpStatus;

public class BorrowingRecordException {

    public static class BorrowingRecordNotFound extends AppException {
        public BorrowingRecordNotFound() {
            super("Borrowing record not found",
                    HttpStatus.NOT_FOUND);
        }
    }

    public static class BookAlreadyBorrowedException extends RuntimeException {
        public BookAlreadyBorrowedException() {
            super("This book is already borrowed");
        }
    }
    
    public static class MaxBorrowedBooksExceededException extends RuntimeException {
        public MaxBorrowedBooksExceededException() {
            super("Patron has reached the maximum number of borrowed books");
        }
    }
    
    public static class OverdueBooksExistException extends RuntimeException {
        public OverdueBooksExistException() {
            super("Patron has overdue books that need to be returned first");
        }
    }
}
