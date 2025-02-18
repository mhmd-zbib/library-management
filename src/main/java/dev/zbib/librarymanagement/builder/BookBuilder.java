package dev.zbib.librarymanagement.builder;


import dev.zbib.librarymanagement.dto.BookRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookBuilder {

    public static Book buildBook(BookRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publicationYear(request.getPublicationYear())
                .ISBN(request.getIsbn())
                .build();
    }

    public static BookResponse buildBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationYear(book.getPublicationYear())
                .isbn(book.getISBN())
                .build();
    }
}