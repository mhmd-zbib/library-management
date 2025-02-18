package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.dto.BookBorrowingRequest;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.entity.BorrowingStatus;
import dev.zbib.librarymanagement.entity.Patron;
import dev.zbib.librarymanagement.exception.BorrowingRecordException;
import dev.zbib.librarymanagement.repository.BorrowingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static dev.zbib.librarymanagement.builder.BorrowingRecordBuilder.buildBorrowingRecord;

@Service
@RequiredArgsConstructor
public class BorrowingRecordService {

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BookService bookService;
    private final PatronService patronService;

    public void borrowBook(BookBorrowingRequest request, UUID bookId, UUID patronId) {
        Book book = bookService.getBookById(bookId);
        Patron patron = patronService.getPatronById(patronId);
        BorrowingRecord borrowingRecord = buildBorrowingRecord(request,
                book,
                patron);
        borrowingRecordRepository.save(borrowingRecord);
    }

    public BorrowingRecord getBorrowingRecordByBookIdAndPatronId(UUID bookId, UUID patronId) {
        return borrowingRecordRepository.findByBookIdAndPatronId(bookId,
                        patronId)
                .orElseThrow(BorrowingRecordException.BorrowingRecordNotFound::new);
    }

    public void returnBook(UUID bookId, UUID patronId) {
        BorrowingRecord borrowingRecord = getBorrowingRecordByBookIdAndPatronId(bookId,
                patronId);
        borrowingRecord.setStatus(BorrowingStatus.RETURNED);
        borrowingRecordRepository.save(borrowingRecord);
    }
}
