package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.BookCreationRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
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

    @PostMapping
    public ResponseEntity<UUID> createBook(@Valid @RequestBody BookCreationRequest request) {
        UUID id = bookService.createBook(request);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping
    public ResponseEntity<Page<BookResponse>> getBooks(Pageable pageable) {
        return ResponseEntity.ok(bookService.getBooks(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> updateBook(@PathVariable UUID id, @Valid @RequestBody BookCreationRequest request) {
        UUID bookId = bookService.updateBook(id,
                request);
        return ResponseEntity.ok(bookId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable UUID id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted");
    }
}
