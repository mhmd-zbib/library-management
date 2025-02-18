package dev.zbib.librarymanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import dev.zbib.librarymanagement.entity.BorrowingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingRecordResponse {
    private UUID id;

    private UUID bookId;
    private String bookTitle;
    private String bookISBN;

    private UUID patronId;
    private String patronName;
    private String patronEmail;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime borrowDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnDate;

    private BorrowingStatus status;
    private Double fineAmount;
    private String notes;

}