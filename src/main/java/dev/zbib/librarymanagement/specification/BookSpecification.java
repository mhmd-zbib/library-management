package dev.zbib.librarymanagement.specification;

import dev.zbib.librarymanagement.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> withPublicationYearGreaterThan(int year) {
        return (root, query, cb) -> cb.greaterThan(root.get("publicationYear"), year);
    }

    public static Specification<Book> withPublicationYearLessThan(int year) {
        return (root, query, cb) -> cb.lessThan(root.get("publicationYear"), year);
    }
} 