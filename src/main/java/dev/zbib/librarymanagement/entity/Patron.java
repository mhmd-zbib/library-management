package dev.zbib.librarymanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "patrons", indexes = {
        @Index(name = "idx_patron_email", columnList = "email"),
        @Index(name = "idx_patron_name", columnList = "first_name, last_name"),
        @Index(name = "idx_patron_membership", columnList = "membership_expiry_date")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patron {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "membership_expiry_date")
    private LocalDateTime membershipExpiryDate;

}