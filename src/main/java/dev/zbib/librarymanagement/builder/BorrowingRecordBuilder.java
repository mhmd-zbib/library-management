package dev.zbib.librarymanagement.builder;

import dev.zbib.librarymanagement.dto.BookBorrowingRequest;
import dev.zbib.librarymanagement.dto.BorrowingRecordResponse;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.entity.BorrowingStatus;
import dev.zbib.librarymanagement.entity.Patron;
import org.springframework.stereotype.Component;

@Component
public class BorrowingRecordBuilder {

    public static BorrowingRecord buildBorrowingRecord(BookBorrowingRequest requestDTO, Book book, Patron patron) {
        return BorrowingRecord.builder()
                .book(book)
                .patron(patron)
                .status(BorrowingStatus.BORROWED)
                .notes(requestDTO.getNotes())
                .returnDate(requestDTO.getReturnDate())
                .borrowDate(requestDTO.getBorrowDate())
                .build();
    }
}