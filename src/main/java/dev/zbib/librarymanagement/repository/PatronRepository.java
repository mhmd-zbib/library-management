package dev.zbib.librarymanagement.repository;

import dev.zbib.librarymanagement.entity.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatronRepository extends JpaRepository<Patron, UUID> {
}
