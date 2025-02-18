package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.PatronCreationRequest;
import dev.zbib.librarymanagement.dto.PatronResponse;
import dev.zbib.librarymanagement.dto.PatronUpdateRequest;
import dev.zbib.librarymanagement.logging.LoggableOperation;
import dev.zbib.librarymanagement.service.PatronService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/patrons")
@RequiredArgsConstructor
public class PatronController {

    private final PatronService patronService;

    @LoggableOperation(
            operationType = "PATRON_CREATE",
            description = "Create a patron ",
            includeResult = true
    )
    @PostMapping
    public ResponseEntity<UUID> createPatron(@Valid @RequestBody PatronCreationRequest request) {
        UUID id = patronService.createPatron(request);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatronResponse> getPatronById(@PathVariable UUID id) {
        return ResponseEntity.ok(patronService.getPatronRequestById(id));
    }

    @GetMapping
    public ResponseEntity<Page<PatronResponse>> getPatrons(Pageable pageable) {
        return ResponseEntity.ok(patronService.getPatrons(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UUID> updatePatron(@PathVariable UUID id, @Valid @RequestBody PatronUpdateRequest request) {
        UUID patronId = patronService.updatePatron(id,
                request);
        return ResponseEntity.ok(patronId);
    }

    @LoggableOperation(
            operationType = "PATRON_DELETE",
            description = "Delete a patron ",
            includeResult = true
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatronById(@PathVariable UUID id) {
        patronService.deletePatron(id);
        return ResponseEntity.ok("Patron deleted");
    }
}