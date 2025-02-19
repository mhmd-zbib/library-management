package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.BookBorrowingRequest;
import dev.zbib.librarymanagement.dto.BorrowingRecordFilter;
import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.logging.LoggableOperation;
import dev.zbib.librarymanagement.service.BorrowingRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Tag(name = "Borrowing", description = "Book borrowing management APIs")
public class BorrowingRecordController {

    private final BorrowingRecordService borrowingRecordService;

    @Operation(
        summary = "Borrow book",
        description = "Create a new borrowing record for a book"
    )
    @ApiResponse(responseCode = "200", description = "Book borrowed successfully")
    @ApiResponse(responseCode = "400", description = "Book not available or invalid request")
    @ApiResponse(responseCode = "404", description = "Book or patron not found")
    @LoggableOperation(operationType = "BOOK_BORROW", description = "Borrow a book", includeResult = true)
    @PostMapping("/borrow/{bookId}/patrons/{patronId}")
    public ResponseEntity<String> borrowBook(
            @Valid @RequestBody BookBorrowingRequest request,
            @Parameter(description = "Book ID", required = true) @PathVariable UUID bookId,
            @Parameter(description = "Patron ID", required = true) @PathVariable UUID patronId) {
        borrowingRecordService.borrowBook(request, bookId, patronId);
        return ResponseEntity.ok("Record updated");
    }

    @Operation(
        summary = "Return book",
        description = "Process a book return"
    )
    @ApiResponse(responseCode = "200", description = "Book returned successfully")
    @ApiResponse(responseCode = "404", description = "Borrowing record not found")
    @LoggableOperation(operationType = "RETURN_BOOK", description = "Returning a borrowed book", includeResult = true)
    @PutMapping("/return/{bookId}/patrons/{patronId}")
    public ResponseEntity<String> returnBook(
            @Parameter(description = "Book ID", required = true) @PathVariable UUID bookId,
            @Parameter(description = "Patron ID", required = true) @PathVariable UUID patronId) {
        borrowingRecordService.returnBook(bookId, patronId);
        return ResponseEntity.ok("Record added");
    }

    @Operation(
        summary = "Get borrowing records",
        description = "Retrieve all borrowing records with filtering and pagination"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved records")
    @GetMapping("/borrow")
    public ResponseEntity<Page<BorrowingRecord>> getBorrowingRecords(
            @Parameter(description = "Filter parameters") 
            @Valid @ModelAttribute BorrowingRecordFilter filter,
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        return ResponseEntity.ok(borrowingRecordService.getBorrowingRecords(filter, pageable));
    }
}
