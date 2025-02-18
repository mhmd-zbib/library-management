package dev.zbib.librarymanagement.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookFilterRequest {
    @Min(value = 0, message = "Year must be positive")
    private int greaterThanYear;

    @Min(value = 0, message = "Year must be positive")
    private int lessThanYear;

} 