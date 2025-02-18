package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.BookBorrowingRequest;
import dev.zbib.librarymanagement.service.BorrowingRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BorrowingRecordController {

    private final BorrowingRecordService borrowingRecordService;

    @PostMapping("/borrow/{bookId}/patrons/{patronId}")
    public ResponseEntity<String> borrowBook(@Valid @RequestBody BookBorrowingRequest request, @PathVariable UUID bookId, @PathVariable UUID patronId) {
        borrowingRecordService.borrowBook(request,
                bookId,
                patronId);
        return ResponseEntity.ok("Record updated");
    }


    @PutMapping("/return/{bookId}/patrons/{patronId}")
    public ResponseEntity<String> returnBook(@PathVariable UUID bookId, @PathVariable UUID patronId) {
        borrowingRecordService.returnBook(bookId,
                patronId);
        return ResponseEntity.ok("Record added");
    }
}
