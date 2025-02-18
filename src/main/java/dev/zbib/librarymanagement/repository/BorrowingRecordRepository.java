package dev.zbib.librarymanagement.repository;

import dev.zbib.librarymanagement.entity.BorrowingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, UUID> {
    Optional<BorrowingRecord> findByBookIdAndPatronId(UUID bookId, UUID patronId);
}
