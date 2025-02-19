package dev.zbib.librarymanagement.config;

import dev.zbib.librarymanagement.entity.*;
import dev.zbib.librarymanagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final BorrowingRecordRepository borrowingRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner initDatabase() {
        return args -> {
            // Create admin user
            User admin = User.builder()
                    .email("admin@library.com")
                    .password(passwordEncoder.encode("admin123"))
                    .build();
            userRepository.save(admin);

            // Create books
            List<Book> books = List.of(
                    Book.builder().title("The Great Gatsby").author("F. Scott Fitzgerald").publicationYear(1925).ISBN("978-0743273565").build(),
                    Book.builder().title("1984").author("George Orwell").publicationYear(1949).ISBN("978-0451524935").build(),
                    Book.builder().title("To Kill a Mockingbird").author("Harper Lee").publicationYear(1960).ISBN("978-0446310789").build(),
                    Book.builder().title("Pride and Prejudice").author("Jane Austen").publicationYear(1813).ISBN("978-0141439518").build(),
                    Book.builder().title("The Hobbit").author("J.R.R. Tolkien").publicationYear(1937).ISBN("978-0547928227").build(),
                    Book.builder().title("The Catcher in the Rye").author("J.D. Salinger").publicationYear(1951).ISBN("978-0316769488").build(),
                    Book.builder().title("Lord of the Flies").author("William Golding").publicationYear(1954).ISBN("978-0399501487").build(),
                    Book.builder().title("Animal Farm").author("George Orwell").publicationYear(1945).ISBN("978-0451526342").build(),
                    Book.builder().title("The Alchemist").author("Paulo Coelho").publicationYear(1988).ISBN("978-0062315007").build(),
                    Book.builder().title("Brave New World").author("Aldous Huxley").publicationYear(1932).ISBN("978-0060850524").build()
            );
            bookRepository.saveAll(books);

            // Create patrons
            List<Patron> patrons = List.of(
                    Patron.builder()
                            .firstName("John").lastName("Doe")
                            .email("john@example.com")
                            .phoneNumber("+1234567890")
                            .address("123 Main St")
                            .membershipExpiryDate(LocalDateTime.now().plusYears(1))
                            .build(),
                    Patron.builder()
                            .firstName("Jane").lastName("Smith")
                            .email("jane@example.com")
                            .phoneNumber("+1234567891")
                            .address("456 Oak Ave")
                            .membershipExpiryDate(LocalDateTime.now().plusYears(1))
                            .build(),
                    Patron.builder()
                            .firstName("Robert").lastName("Johnson")
                            .email("robert@example.com")
                            .phoneNumber("+1234567892")
                            .address("789 Pine Rd")
                            .membershipExpiryDate(LocalDateTime.now().plusYears(1))
                            .build(),
                    Patron.builder()
                            .firstName("Maria").lastName("Garcia")
                            .email("maria@example.com")
                            .phoneNumber("+1234567893")
                            .address("321 Elm St")
                            .membershipExpiryDate(LocalDateTime.now().plusYears(1))
                            .build(),
                    Patron.builder()
                            .firstName("David").lastName("Brown")
                            .email("david@example.com")
                            .phoneNumber("+1234567894")
                            .address("654 Maple Ln")
                            .membershipExpiryDate(LocalDateTime.now().plusYears(1))
                            .build()
            );
            patronRepository.saveAll(patrons);

            // Create borrowing records
            LocalDateTime now = LocalDateTime.now();
            List<BorrowingRecord> borrowingRecords = List.of(
                    BorrowingRecord.builder()
                            .book(books.get(0))
                            .patron(patrons.get(0))
                            .borrowDate(now.minusDays(10))
                            .dueDate(now.plusDays(4))
                            .status(BorrowingStatus.BORROWED)
                            .isOverdue(false)
                            .build(),
                    BorrowingRecord.builder()
                            .book(books.get(1))
                            .patron(patrons.get(1))
                            .borrowDate(now.minusDays(15))
                            .dueDate(now.minusDays(1))
                            .status(BorrowingStatus.BORROWED)
                            .isOverdue(true)
                            .build(),
                    BorrowingRecord.builder()
                            .book(books.get(2))
                            .patron(patrons.get(2))
                            .borrowDate(now.minusDays(20))
                            .returnDate(now.minusDays(5))
                            .dueDate(now.minusDays(6))
                            .status(BorrowingStatus.RETURNED)
                            .isOverdue(false)
                            .build()
            );
            borrowingRecordRepository.saveAll(borrowingRecords);
        };
    }
} 