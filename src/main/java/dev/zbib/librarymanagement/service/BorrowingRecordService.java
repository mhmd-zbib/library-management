package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.dto.BookBorrowingRequest;
import dev.zbib.librarymanagement.dto.BorrowingRecordFilter;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.entity.BorrowingStatus;
import dev.zbib.librarymanagement.entity.Patron;
import dev.zbib.librarymanagement.exception.BorrowingRecordException;
import dev.zbib.librarymanagement.repository.BorrowingRecordRepository;
import dev.zbib.librarymanagement.specification.BorrowingRecordSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.zbib.librarymanagement.builder.BorrowingRecordBuilder.buildBorrowingRecord;

@Service
@RequiredArgsConstructor
public class BorrowingRecordService {

    private static final int MAX_BORROWED_BOOKS = 5;
    private static final int BORROW_DURATION_DAYS = 14;

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BookService bookService;
    private final PatronService patronService;

    @Transactional
    public void borrowBook(BookBorrowingRequest request, UUID bookId, UUID patronId) {
        validateBorrowing(bookId,
                patronId);

        Book book = bookService.getBookById(bookId);
        Patron patron = patronService.getPatronById(patronId);

        BorrowingRecord borrowingRecord = buildBorrowingRecord(request,
                book,
                patron);
        borrowingRecord.setDueDate(LocalDateTime.now()
                .plusDays(BORROW_DURATION_DAYS));
        borrowingRecord.setOverdue(false);

        borrowingRecordRepository.save(borrowingRecord);
    }

    private void validateBorrowing(UUID bookId, UUID patronId) {
        if (borrowingRecordRepository.existsByBookIdAndStatus(bookId,
                BorrowingStatus.BORROWED)) {
            throw new BorrowingRecordException.BookAlreadyBorrowedException();
        }
        if (borrowingRecordRepository.countActiveBooksByPatron(patronId) >= MAX_BORROWED_BOOKS) {
            throw new BorrowingRecordException.MaxBorrowedBooksExceededException();
        }
        if (borrowingRecordRepository.countOverdueBooksByPatron(patronId) > 0) {
            throw new BorrowingRecordException.OverdueBooksExistException();
        }
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

    public Page<BorrowingRecord> getBorrowingRecords(BorrowingRecordFilter filter, Pageable pageable) {
        return borrowingRecordRepository.findAll(
                BorrowingRecordSpecification.withFilter(filter),
                pageable
        );
    }
}
