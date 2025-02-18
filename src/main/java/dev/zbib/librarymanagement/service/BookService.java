package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.builder.BookBuilder;
import dev.zbib.librarymanagement.dto.BookRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.exception.BookException;
import dev.zbib.librarymanagement.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static dev.zbib.librarymanagement.builder.BookBuilder.buildBook;
import static dev.zbib.librarymanagement.builder.BookBuilder.buildBookResponse;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public UUID createBook(BookRequest request) {
        Book book = buildBook(request);
        return bookRepository.save(book)
                .getId();
    }

    public BookResponse getBookById(UUID id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(BookException.BookNotFound::new);
        return buildBookResponse(book);
    }

    public Page<BookResponse> getBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(BookBuilder::buildBookResponse);
    }

    public UUID updateBook(UUID id, BookRequest request) {
        Book existingBook = findBookById(id);
        updateBookFields(existingBook,
                request);
        return bookRepository.save(existingBook)
                .getId();
    }

    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

    private Book findBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(BookException.BookNotFound::new);
    }

    private void updateBookFields(Book book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setISBN(request.getIsbn());
    }
}
