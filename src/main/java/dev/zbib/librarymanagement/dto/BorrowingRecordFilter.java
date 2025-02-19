package dev.zbib.librarymanagement.dto;

import dev.zbib.librarymanagement.entity.BorrowingStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BorrowingRecordFilter {
    private UUID patronId;
    private UUID bookId;
    private BorrowingStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private Boolean isOverdue;
} 