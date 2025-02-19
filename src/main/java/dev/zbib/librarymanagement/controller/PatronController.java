package dev.zbib.librarymanagement.controller;

import dev.zbib.librarymanagement.dto.PatronCreationRequest;
import dev.zbib.librarymanagement.dto.PatronResponse;
import dev.zbib.librarymanagement.dto.PatronUpdateRequest;
import dev.zbib.librarymanagement.logging.LoggableOperation;
import dev.zbib.librarymanagement.service.PatronService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Patrons", description = "Patron management APIs")
public class PatronController {

    private final PatronService patronService;

    @Operation(
        summary = "Create patron",
        description = "Register a new patron"
    )
    @ApiResponse(responseCode = "200", description = "Patron created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @LoggableOperation(operationType = "PATRON_CREATE", description = "Create a patron", includeResult = true)
    @PostMapping
    public ResponseEntity<UUID> createPatron(@Valid @RequestBody PatronCreationRequest request) {
        UUID id = patronService.createPatron(request);
        return ResponseEntity.ok(id);
    }

    @Operation(
        summary = "Get patron by ID",
        description = "Retrieve a patron's details by their ID"
    )
    @ApiResponse(responseCode = "200", description = "Patron found")
    @ApiResponse(responseCode = "404", description = "Patron not found")
    @GetMapping("/{id}")
    public ResponseEntity<PatronResponse> getPatronById(
            @Parameter(description = "Patron ID", required = true) 
            @PathVariable UUID id) {
        return ResponseEntity.ok(patronService.getPatronRequestById(id));
    }

    @Operation(
        summary = "Get all patrons",
        description = "Retrieve all patrons with pagination"
    )
    @ApiResponse(responseCode = "200", description = "Successfully retrieved patrons")
    @GetMapping
    public ResponseEntity<Page<PatronResponse>> getPatrons(
            @Parameter(description = "Pagination parameters") 
            Pageable pageable) {
        return ResponseEntity.ok(patronService.getPatrons(pageable));
    }

    @Operation(
        summary = "Update patron",
        description = "Update an existing patron's details"
    )
    @ApiResponse(responseCode = "200", description = "Patron updated successfully")
    @ApiResponse(responseCode = "404", description = "Patron not found")
    @PutMapping("/{id}")
    public ResponseEntity<UUID> updatePatron(
            @Parameter(description = "Patron ID", required = true) 
            @PathVariable UUID id,
            @Valid @RequestBody PatronUpdateRequest request) {
        UUID patronId = patronService.updatePatron(id, request);
        return ResponseEntity.ok(patronId);
    }

    @Operation(
        summary = "Delete patron",
        description = "Delete a patron by their ID"
    )
    @ApiResponse(responseCode = "200", description = "Patron deleted successfully")
    @ApiResponse(responseCode = "404", description = "Patron not found")
    @LoggableOperation(operationType = "PATRON_DELETE", description = "Delete a patron", includeResult = true)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatronById(
            @Parameter(description = "Patron ID", required = true) 
            @PathVariable UUID id) {
        patronService.deletePatron(id);
        return ResponseEntity.ok("Patron deleted");
    }
}