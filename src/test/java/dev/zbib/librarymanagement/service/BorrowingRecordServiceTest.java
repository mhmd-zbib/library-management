package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.dto.BookBorrowingRequest;
import dev.zbib.librarymanagement.dto.BorrowingRecordFilter;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.entity.BorrowingStatus;
import dev.zbib.librarymanagement.entity.Patron;
import dev.zbib.librarymanagement.exception.BorrowingRecordException;
import dev.zbib.librarymanagement.repository.BorrowingRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingRecordServiceTest {

    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;

    @Mock
    private BookService bookService;

    @Mock
    private PatronService patronService;

    @InjectMocks
    private BorrowingRecordService borrowingRecordService;

    private UUID bookId;
    private UUID patronId;
    private Book testBook;
    private Patron testPatron;
    private BorrowingRecord testBorrowingRecord;
    private BookBorrowingRequest borrowingRequest;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        patronId = UUID.randomUUID();

        testBook = Book.builder()
                .id(bookId)
                .title("Test Book")
                .author("Test Author")
                .ISBN("1234567890")
                .publicationYear(2023)
                .build();

        testPatron = Patron.builder()
                .id(patronId)
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .build();

        testBorrowingRecord = BorrowingRecord.builder()
                .id(UUID.randomUUID())
                .book(testBook)
                .patron(testPatron)
                .borrowDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14))
                .status(BorrowingStatus.BORROWED)
                .build();

        borrowingRequest = BookBorrowingRequest.builder()
                .notes("Test borrowing")
                .build();
    }

    @Test
    void borrowBook_WhenValidRequest_ShouldCreateBorrowingRecord() {
        when(bookService.getBookById(bookId)).thenReturn(testBook);
        when(patronService.getPatronById(patronId)).thenReturn(testPatron);
        when(borrowingRecordRepository.existsByBookIdAndStatus(bookId, BorrowingStatus.BORROWED))
                .thenReturn(false);
        when(borrowingRecordRepository.countActiveBooksByPatron(patronId)).thenReturn(0L);
        when(borrowingRecordRepository.countOverdueBooksByPatron(patronId)).thenReturn(0L);

        borrowingRecordService.borrowBook(borrowingRequest, bookId, patronId);

        verify(borrowingRecordRepository, times(1)).save(any(BorrowingRecord.class));
    }

    @Test
    void borrowBook_WhenBookAlreadyBorrowed_ShouldThrowException() {
        when(borrowingRecordRepository.existsByBookIdAndStatus(bookId, BorrowingStatus.BORROWED))
                .thenReturn(true);

        assertThrows(BorrowingRecordException.BookAlreadyBorrowedException.class, () ->
                borrowingRecordService.borrowBook(borrowingRequest, bookId, patronId)
        );

        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
    }

    @Test
    void borrowBook_WhenMaxBooksExceeded_ShouldThrowException() {
        when(borrowingRecordRepository.existsByBookIdAndStatus(bookId, BorrowingStatus.BORROWED))
                .thenReturn(false);
        when(borrowingRecordRepository.countActiveBooksByPatron(patronId)).thenReturn(5L);

        assertThrows(BorrowingRecordException.MaxBorrowedBooksExceededException.class, () ->
                borrowingRecordService.borrowBook(borrowingRequest, bookId, patronId)
        );

        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
    }

    @Test
    void borrowBook_WhenHasOverdueBooks_ShouldThrowException() {
        when(borrowingRecordRepository.existsByBookIdAndStatus(bookId, BorrowingStatus.BORROWED))
                .thenReturn(false);
        when(borrowingRecordRepository.countActiveBooksByPatron(patronId)).thenReturn(1L);
        when(borrowingRecordRepository.countOverdueBooksByPatron(patronId)).thenReturn(1L);

        assertThrows(BorrowingRecordException.OverdueBooksExistException.class, () ->
                borrowingRecordService.borrowBook(borrowingRequest, bookId, patronId)
        );

        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
    }

    @Test
    void getBorrowingRecordByBookIdAndPatronId_WhenExists_ShouldReturnRecord() {
        when(borrowingRecordRepository.findByBookIdAndPatronId(bookId, patronId))
                .thenReturn(Optional.of(testBorrowingRecord));

        BorrowingRecord result = borrowingRecordService
                .getBorrowingRecordByBookIdAndPatronId(bookId, patronId);

        assertNotNull(result);
        assertEquals(testBorrowingRecord.getId(), result.getId());
    }

    @Test
    void getBorrowingRecordByBookIdAndPatronId_WhenNotExists_ShouldThrowException() {
        when(borrowingRecordRepository.findByBookIdAndPatronId(bookId, patronId))
                .thenReturn(Optional.empty());

        assertThrows(BorrowingRecordException.BorrowingRecordNotFound.class, () ->
                borrowingRecordService.getBorrowingRecordByBookIdAndPatronId(bookId, patronId)
        );
    }

    @Test
    void returnBook_WhenValidRequest_ShouldUpdateStatus() {
        when(borrowingRecordRepository.findByBookIdAndPatronId(bookId, patronId))
                .thenReturn(Optional.of(testBorrowingRecord));

        borrowingRecordService.returnBook(bookId, patronId);

        assertEquals(BorrowingStatus.RETURNED, testBorrowingRecord.getStatus());
        verify(borrowingRecordRepository, times(1)).save(testBorrowingRecord);
    }

    @Test
    void returnBook_WhenRecordNotFound_ShouldThrowException() {
        when(borrowingRecordRepository.findByBookIdAndPatronId(bookId, patronId))
                .thenReturn(Optional.empty());

        assertThrows(BorrowingRecordException.BorrowingRecordNotFound.class, () ->
                borrowingRecordService.returnBook(bookId, patronId)
        );

        verify(borrowingRecordRepository, never()).save(any(BorrowingRecord.class));
    }

    @Test
    void getBorrowingRecords_ShouldReturnFilteredRecords() {
        Pageable pageable = PageRequest.of(0, 10);
        BorrowingRecordFilter filter = BorrowingRecordFilter.builder()
                .status(BorrowingStatus.BORROWED)
                .build();
        Page<BorrowingRecord> expectedPage = new PageImpl<>(List.of(testBorrowingRecord));

        when(borrowingRecordRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<BorrowingRecord> result = borrowingRecordService.getBorrowingRecords(filter, pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        verify(borrowingRecordRepository, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getBorrowingRecords_WithEmptyFilter_ShouldReturnAllRecords() {
        Pageable pageable = PageRequest.of(0, 10);
        BorrowingRecordFilter emptyFilter = BorrowingRecordFilter.builder().build();
        Page<BorrowingRecord> expectedPage = new PageImpl<>(List.of(testBorrowingRecord));

        when(borrowingRecordRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        Page<BorrowingRecord> result = borrowingRecordService.getBorrowingRecords(emptyFilter, pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
    }
} 