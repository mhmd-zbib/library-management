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
    private static final String BOOK_CACHE = "book-details";
    private static final String BOOKS_LIST_CACHE = "books-list";

    @CachePut(value = BOOK_CACHE, key = "#result.id")
    public BookResponse createBook(BookCreationRequest request) {
        Book book = buildBook(request);
        Book savedBook = bookRepository.save(book);
        return buildBookResponse(savedBook);
    }

    @Cacheable(
            value = BOOK_CACHE,
            key = "#id",
            unless = "#result == null"
    )
    public BookResponse getBookRequestById(UUID id) {
        Book book = getBookById(id);
        return buildBookResponse(book);
    }

    @Cacheable(
            value = BOOK_CACHE,
            key = "#id",
            unless = "#result == null"
    )
    public Book getBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(BookException.BookNotFound::new);
    }

    @Cacheable(
            value = BOOKS_LIST_CACHE,
            key = "T(java.lang.String).format('filter_%d_%d_page_%d_size_%d', " +
                    "#filterRequest.greaterThanYear, " +
                    "#filterRequest.lessThanYear, " +
                    "#pageable.pageNumber, " +
                    "#pageable.pageSize)",
            unless = "#result.isEmpty()"
    )
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

    @CachePut(
            value = BOOK_CACHE,
            key = "#id",
            condition = "#result != null"
    )
    public BookResponse updateBook(UUID id, BookUpdateRequest request) {
        Book existingBook = findBookById(id);
        updateBookFields(existingBook,
                request);
        Book updatedBook = bookRepository.save(existingBook);
        clearBooksListCache();
        return buildBookResponse(updatedBook);
    }

    @CacheEvict(value = BOOK_CACHE, key = "#id")
    public void deleteBook(UUID id) {
        bookRepository.deleteById(id);
        clearBooksListCache();
    }

    @CacheEvict(value = BOOKS_LIST_CACHE, allEntries = true)
    public void clearBooksListCache() {
        // Method intentionally left empty
    }

    private void updateBookFields(Book book, BookUpdateRequest request) {
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        Integer publicationYear = request.getPublicationYear();
        if (publicationYear != null && publicationYear > 0) {
            book.setPublicationYear(publicationYear);
        }
        if (request.getIsbn() != null) {
            book.setISBN(request.getIsbn());
        }
    }

    private Book findBookById(UUID id) {
        return bookRepository.findById(id)
                .orElseThrow(BookException.BookNotFound::new);
    }
}
