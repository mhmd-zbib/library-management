package dev.zbib.librarymanagement.repository;

import dev.zbib.librarymanagement.entity.BorrowingRecord;
import dev.zbib.librarymanagement.entity.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, UUID>, 
        JpaSpecificationExecutor<BorrowingRecord> {
    Optional<BorrowingRecord> findByBookIdAndPatronId(UUID bookId, UUID patronId);
    
    @Query("SELECT COUNT(br) FROM BorrowingRecord br WHERE br.patron.id = :patronId AND br.status = 'BORROWED'")
    long countActiveBooksByPatron(UUID patronId);
    
    @Query("SELECT COUNT(br) FROM BorrowingRecord br WHERE br.patron.id = :patronId AND br.isOverdue = true")
    long countOverdueBooksByPatron(UUID patronId);
    
    boolean existsByBookIdAndStatus(UUID bookId, BorrowingStatus status);
}
