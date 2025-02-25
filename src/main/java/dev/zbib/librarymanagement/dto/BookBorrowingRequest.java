package dev.zbib.librarymanagement.dto;


import dev.zbib.librarymanagement.entity.BorrowingStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookBorrowingRequest {

    @NotNull(message = "Borrow date cannot be null")
    @PastOrPresent(message = "Borrow date cannot be in the future")
    private LocalDateTime borrowDate;

    @NotNull(message = "Due date cannot be null")
    @Future(message = "Due date must be in the future")
    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    @NotNull(message = "Status cannot be null")
    private BorrowingStatus status;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}