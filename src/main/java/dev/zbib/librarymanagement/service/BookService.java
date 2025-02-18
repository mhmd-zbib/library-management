package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.builder.BookBuilder;
import dev.zbib.librarymanagement.dto.BookCreationRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.dto.BookUpdateRequest;
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

    public UUID createBook(BookCreationRequest request) {
        Book book = buildBook(request);
        return bookRepository.save(book)
                .getId();
    }

    public BookResponse getBookRequestById(UUID id) {
        Book book = getBookById(id);
        return buildBookResponse(book);
    }

    public Book getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(BookException.BookNotFound::new);
    }

    public Page<BookResponse> getBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findAll(pageable);
        return books.map(BookBuilder::buildBookResponse);
    }

    public UUID updateBook(UUID id, BookUpdateRequest request) {
        Book existingBook = findBookById(id);
        if (request.getTitle() != null) {
            existingBook.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            existingBook.setAuthor(request.getAuthor());
        }
        if (request.getPublicationYear() > 0) {
            existingBook.setPublicationYear(request.getPublicationYear());
        }
        if (request.getIsbn() != null) {
            existingBook.setISBN(request.getIsbn());
        }
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
}
