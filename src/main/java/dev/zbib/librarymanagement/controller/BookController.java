package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.BookCreationRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.dto.BookUpdateRequest;
import dev.zbib.librarymanagement.dto.BookFilterRequest;
import dev.zbib.librarymanagement.logging.LogLevel;
import dev.zbib.librarymanagement.logging.LoggableOperation;
import dev.zbib.librarymanagement.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @LoggableOperation(
            operationType = "BOOK_CREATE",
            description = "Create new book",
            includeResult = true
    )
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(bookService.createBook(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookRequestById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(@Valid @ModelAttribute BookFilterRequest filterRequest, Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooks(filterRequest,
                pageable));
    }

    @LoggableOperation(
            operationType = "BOOK_UPDATE",
            description = "Update existing book",
            includeResult = true
    )
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable UUID id, @Valid @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id,
                request));
    }

    @LoggableOperation(
            operationType = "BOOK_DELETE",
            description = "Delete book by ID",
            level = LogLevel.WARN
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted");
    }
}
