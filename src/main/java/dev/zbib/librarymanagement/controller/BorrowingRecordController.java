package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.BookBorrowingRequest;
import dev.zbib.librarymanagement.dto.BorrowingRecordFilter;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.logging.LoggableOperation;
import dev.zbib.librarymanagement.service.BorrowingRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/borrowing-records")
public class BorrowingRecordController {

    private final BorrowingRecordService borrowingRecordService;

    @LoggableOperation(
            operationType = "BOOK_BORROW",
            description = "Borrow a book",
            includeResult = true
    )
    @PostMapping("/borrow/{bookId}/patrons/{patronId}")
    public ResponseEntity<String> borrowBook(@Valid @RequestBody BookBorrowingRequest request, @PathVariable UUID bookId, @PathVariable UUID patronId) {
        borrowingRecordService.borrowBook(request,
                bookId,
                patronId);
        return ResponseEntity.ok("Record updated");
    }


    @LoggableOperation(
            operationType = "RETURN_BOOK",
            description = "Returning a borrwed book",
            includeResult = true
    )
    @PutMapping("/return/{bookId}/patrons/{patronId}")
    public ResponseEntity<String> returnBook(@PathVariable UUID bookId, @PathVariable UUID patronId) {
        borrowingRecordService.returnBook(bookId,
                patronId);
        return ResponseEntity.ok("Record added");
    }

    @GetMapping
    public ResponseEntity<Page<BorrowingRecord>> getBorrowingRecords(
            @Valid @ModelAttribute BorrowingRecordFilter filter,
            Pageable pageable) {
        return ResponseEntity.ok(borrowingRecordService.getBorrowingRecords(filter,
                pageable));
    }
}
