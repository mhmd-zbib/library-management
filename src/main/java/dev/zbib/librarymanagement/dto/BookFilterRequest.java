package dev.zbib.librarymanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookFilterRequest {
    @Min(value = 0, message = "Year must be positive")
    private int greaterThanYear;

    @Min(value = 0, message = "Year must be positive")
    private int lessThanYear;

} 