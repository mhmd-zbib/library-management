package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.BookCreationRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.dto.BookUpdateRequest;
import dev.zbib.librarymanagement.dto.BookFilterRequest;
import dev.zbib.librarymanagement.logging.LogLevel;
import dev.zbib.librarymanagement.logging.LoggableOperation;
import dev.zbib.librarymanagement.service.BookService;
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
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Book management APIs")
public class BookController {

    private final BookService bookService;

    @Operation(
        summary = "Create new book",
        description = "Add a new book to the library"
    )
    @ApiResponse(responseCode = "200", description = "Book created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @LoggableOperation(operationType = "BOOK_CREATE", description = "Create new book", includeResult = true)
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookCreationRequest request) {
        return ResponseEntity.ok(bookService.createBook(request));
    }

    @Operation(
        summary = "Get book by ID",
        description = "Retrieve a book's details by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Book found")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(
            @Parameter(description = "Book ID", required = true) 
            @PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookRequestById(id));
    }

    @Operation(
        summary = "Get all books",
        description = "Retrieve all books with filtering and pagination"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved books")
    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(
            @Parameter(description = "Filter parameters") 
            @Valid @ModelAttribute BookFilterRequest filterRequest,
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooks(filterRequest, pageable));
    }

    @Operation(
        summary = "Update book",
        description = "Update an existing book's details"
    )
    @ApiResponse(responseCode = "200", description = "Book updated successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @LoggableOperation(operationType = "BOOK_UPDATE", description = "Update existing book", includeResult = true)
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(
            @Parameter(description = "Book ID", required = true) 
            @PathVariable UUID id,
            @Valid @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @Operation(
        summary = "Delete book",
        description = "Delete a book by its ID"
    )
    @ApiResponse(responseCode = "200", description = "Book deleted successfully")
    @ApiResponse(responseCode = "404", description = "Book not found")
    @LoggableOperation(operationType = "BOOK_DELETE", description = "Delete book by ID", level = LogLevel.WARN)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(
            @Parameter(description = "Book ID", required = true) 
            @PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted");
    }
}
