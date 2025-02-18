package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.builder.BookBuilder;
import dev.zbib.librarymanagement.dto.BookCreationRequest;
import dev.zbib.librarymanagement.dto.BookFilterRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.dto.BookUpdateRequest;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.exception.BookException;
import dev.zbib.librarymanagement.repository.BookRepository;
import dev.zbib.librarymanagement.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;

import java.util.UUID;

import static dev.zbib.librarymanagement.builder.BookBuilder.buildBook;
import static dev.zbib.librarymanagement.builder.BookBuilder.buildBookResponse;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @CachePut(value = "books", key = "#result")
    public UUID createBook(BookCreationRequest request) {
        Book book = buildBook(request);
        return bookRepository.save(book)
                .getId();
    }

    @Cacheable(value = "books", key = "#id")
    public BookResponse getBookRequestById(UUID id) {
        Book book = getBookById(id);
        return buildBookResponse(book);
    }

    @Cacheable(value = "books", key = "#id")
    public Book getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(BookException.BookNotFound::new);
    }

    @Cacheable(value = "booksByFilter", key = "T(java.lang.String).format('%s-%s-%s-%d', #filterRequest.greaterThanYear, #filterRequest.lessThanYear, #pageable.pageNumber, #pageable.pageSize)")
    public Page<BookResponse> getBooks(BookFilterRequest filterRequest, Pageable pageable) {
        Specification<Book> spec = Specification.where(null);

        if (filterRequest.getGreaterThanYear() > 0) {
            spec = spec.and(BookSpecification.withPublicationYearGreaterThan(filterRequest.getGreaterThanYear()));
        }
        if (filterRequest.getLessThanYear() > 0) {
            spec = spec.and(BookSpecification.withPublicationYearLessThan(filterRequest.getLessThanYear()));
        }

        Page<Book> books = bookRepository.findAll(spec,
                pageable);
        return books.map(BookBuilder::buildBookResponse);
    }

    @CachePut(value = "books", key = "#id")
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

    @CacheEvict(value = {"books", "booksByFilter"}, allEntries = true)
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
    }

    private Book findBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(BookException.BookNotFound::new);
    }
}
