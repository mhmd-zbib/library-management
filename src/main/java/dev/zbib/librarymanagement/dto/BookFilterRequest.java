package dev.zbib.librarymanagement.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookFilterRequest {
    @Min(value = 0, message = "Year must be positive")
    private int fromYear;

    @Min(value = 0, message = "Year must be positive")
    private int toYear;

} 