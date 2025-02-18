package dev.zbib.librarymanagement.dto;


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookUpdateRequest {

    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    private String title;

    @Size(min = 1, max = 100, message = "Author name must be between 1 and 100 characters")
    private String author;

    private int publicationYear;

    @Pattern(regexp = "^(?:ISBN(?:-1[03])?:? )?(?=[0-9X]{10}$|(?=(?:[0-9]+[- ]){3})[- 0-9X]{13}$|97[89][0-9]{10}$|(?=(?:[0-9]+[- ]){4})[- 0-9]{17}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?[0-9]+[- ]?[0-9]+[- ]?[0-9X]$",
            message = "Invalid ISBN format")
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;

}
