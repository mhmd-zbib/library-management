package dev.zbib.librarymanagement.mapper;


import dev.zbib.librarymanagement.dto.BookRequest;
import dev.zbib.librarymanagement.dto.BookResponse;
import dev.zbib.librarymanagement.entity.Book;
import org.springframework.stereotype.Component;

@Component
public class BookBuilder {

    public static Book buildBookRequest(BookRequest request) {
        return Book.builder()
                .title(request.getTitle())
                .author(request.getAuthor())
                .publicationYear(request.getPublicationYear())
                .ISBN(request.getISBN())
                .build();
    }

    public static BookResponse buildBookResponse(Book book) {
        return BookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .publicationYear(book.getPublicationYear())
                .ISBN(book.getISBN())
                .build();
    }
}