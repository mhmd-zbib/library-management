package dev.zbib.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "borrowing_records")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BorrowingRecord {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patron_id", nullable = false)
    private Patron patron;

    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;


    @Column(name = "return_date")
    private LocalDateTime returnDate;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;

    @Column(name = "notes", length = 500)
    private String notes;

}