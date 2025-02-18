package dev.zbib.librarymanagement.dto;

import dev.zbib.librarymanagement.entity.BorrowingStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class BorrowingRecordFilter {
    private BorrowingStatus status;
    private UUID bookId;
    private UUID patronId;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;
    
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
} 