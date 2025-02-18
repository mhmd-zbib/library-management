package dev.zbib.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.UUID;

@Data
@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_book_publication_year", columnList = "publicationYear"),
        @Index(name = "idx_book_isbn", columnList = "isbn", unique = true),
        @Index(name = "idx_book_title", columnList = "title"),
        @Index(name = "idx_book_author", columnList = "author")})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "author", nullable = false, length = 100)
    private String author;

    @Column(name = "publication_year", nullable = false)
    private int publicationYear;

    @Column(name = "isbn", nullable = false, unique = true, length = 20)
    private String ISBN;
}