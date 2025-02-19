package dev.zbib.librarymanagement.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
public class BookUpdateRequest {

    @Pattern(regexp = "^$|^[\\p{L}\\p{N}\\s.,!?'\"()-:;]+$",
            message = "Title can only contain letters, numbers, spaces and basic punctuation")
    @Length(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Pattern(regexp = "^$|^[\\p{L}\\s.'-]+$",
            message = "Author name can only contain letters, spaces, dots, hyphens and apostrophes")
    @Length(max = 100, message = "Author name must not exceed 100 characters")
    private String author;

    @Min(value = 1000, message = "Publication year must be after 1000")
    @Max(value = 9999, message = "Publication year must be a valid 4-digit year")
    private Integer publicationYear;

    @Pattern(regexp = "^$|^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|97[89][0-9]{10}$).*$",
            message = "Invalid ISBN format. Must be a valid ISBN-10 or ISBN-13")
    @Length(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;
    
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
    
    @Length(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @Pattern(regexp = "^$|^[A-Z]{1,5}$", message = "Genre code must be 1-5 uppercase letters")
    private String genreCode;
}
