package dev.zbib.librarymanagement.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {
    private UUID id;
    private String title;
    private String author;
    private int publicationYear;
    private String isbn;
}
