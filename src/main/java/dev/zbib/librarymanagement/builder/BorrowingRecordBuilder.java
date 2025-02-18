package dev.zbib.librarymanagement.builder;

import dev.zbib.librarymanagement.dto.BorrowingRecordRequest;
import dev.zbib.librarymanagement.dto.BorrowingRecordResponse;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.entity.Patron;
import org.springframework.stereotype.Component;

@Component
public class BorrowingRecordBuilder {

    public static BorrowingRecord buildBorrowingRecord(BorrowingRecordRequest requestDTO, Book book, Patron patron) {
        return BorrowingRecord.builder()
                .book(book)
                .patron(patron)
                .build();
    }

    public static BorrowingRecordResponse buildBorrowingRecordResponse(BorrowingRecord record) {
        return BorrowingRecordResponse.builder()
                .id(record.getId())
                .bookId(record.getBook().getId())
                .bookTitle(record.getBook().getTitle())
                .bookISBN(record.getBook().getISBN())
                .patronId(record.getPatron().getId())
                .patronName(record.getPatron().getFirstName() + " " + record.getPatron().getLastName())
                .patronEmail(record.getPatron().getEmail())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .fineAmount(record.getFineAmount())
                .notes(record.getNotes())
                .build();
    }
}