package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.dto.BookCreationRequest;
import dev.zbib.librarymanagement.dto.BookFilterRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.dto.BookUpdateRequest;
import dev.zbib.librarymanagement.entity.Book;
import dev.zbib.librarymanagement.exception.BookException;
import dev.zbib.librarymanagement.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;
    private BookCreationRequest creationRequest;
    private BookUpdateRequest updateRequest;
    private UUID bookId;

    @BeforeEach
    void setUp() {
        bookId = UUID.randomUUID();
        testBook = Book.builder()
                .id(bookId)
                .title("Test Book")
                .author("Test Author")
                .ISBN("1234567890")
                .publicationYear(2023)
                .build();

        creationRequest = BookCreationRequest.builder()
                .title("New Book")
                .author("New Author")
                .isbn("0987654321")
                .publicationYear(2024)
                .build();

        updateRequest = BookUpdateRequest.builder()
                .title("Updated Book")
                .author("Updated Author")
                .isbn("1111111111")
                .publicationYear(2025)
                .build();
    }

    @Test
    void createBook_ShouldReturnBookResponse() {
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookResponse response = bookService.createBook(creationRequest);

        assertNotNull(response);
        assertEquals(testBook.getId(), response.getId());
        assertEquals(testBook.getTitle(), response.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void getBookById_WhenBookExists_ShouldReturnBook() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        Book result = bookService.getBookById(bookId);

        assertNotNull(result);
        assertEquals(testBook.getId(), result.getId());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookById_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookException.BookNotFound.class, () -> 
            bookService.getBookById(bookId)
        );
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBookRequestById_WhenBookExists_ShouldReturnBookResponse() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));

        BookResponse response = bookService.getBookRequestById(bookId);

        assertNotNull(response);
        assertEquals(testBook.getId(), response.getId());
        assertEquals(testBook.getTitle(), response.getTitle());
        verify(bookRepository, times(1)).findById(bookId);
    }

    @Test
    void getBooks_ShouldReturnPageOfBookResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        BookFilterRequest filterRequest = BookFilterRequest.builder()
                .fromYear(2000)
                .toYear(2025)
                .build();
        Page<Book> bookPage = new PageImpl<>(List.of(testBook));

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bookPage);

        Page<BookResponse> result = bookService.getBooks(filterRequest, pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        verify(bookRepository, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBookResponse() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        BookResponse response = bookService.updateBook(bookId, updateRequest);

        assertNotNull(response);
        assertEquals(bookId, response.getId());
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowException() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThrows(BookException.BookNotFound.class, () ->
            bookService.updateBook(bookId, updateRequest)
        );
        verify(bookRepository, times(1)).findById(bookId);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void deleteBook_ShouldCallRepositoryDelete() {
        bookService.deleteBook(bookId);

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    @Test
    void updateBookFields_ShouldUpdateOnlyProvidedFields() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        
        BookUpdateRequest partialUpdate = BookUpdateRequest.builder()
                .title("New Title")
                .build();

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.updateBook(bookId, partialUpdate);

        assertNotNull(response);
        assertEquals("New Title", response.getTitle());
        assertEquals(testBook.getAuthor(), response.getAuthor());
        assertEquals(testBook.getISBN(), response.getIsbn());
        assertEquals(testBook.getPublicationYear(), response.getPublicationYear());
    }

    @Test
    void updateBookFields_WithZeroPublicationYear_ShouldNotUpdate() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        
        BookUpdateRequest partialUpdate = BookUpdateRequest.builder()
                .publicationYear(0)
                .build();

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.updateBook(bookId, partialUpdate);

        assertEquals(testBook.getPublicationYear(), response.getPublicationYear());
    }

    @Test
    void updateBookFields_WithValidPublicationYear_ShouldUpdate() {
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(testBook));
        
        int newYear = 2025;
        BookUpdateRequest partialUpdate = BookUpdateRequest.builder()
                .publicationYear(newYear)
                .build();

        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookResponse response = bookService.updateBook(bookId, partialUpdate);

        assertEquals(newYear, response.getPublicationYear());
    }

    @Test
    void getBooks_WithNoFilters_ShouldReturnAllBooks() {
        Pageable pageable = PageRequest.of(0, 10);
        BookFilterRequest emptyFilter = BookFilterRequest.builder().build();
        Page<Book> bookPage = new PageImpl<>(List.of(testBook));

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(bookPage);

        Page<BookResponse> result = bookService.getBooks(emptyFilter, pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        verify(bookRepository, times(1))
                .findAll(any(Specification.class), any(Pageable.class));
    }
} 