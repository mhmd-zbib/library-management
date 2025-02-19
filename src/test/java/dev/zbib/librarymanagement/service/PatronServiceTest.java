package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.dto.PatronCreationRequest;
import dev.zbib.librarymanagement.dto.PatronResponse;
import dev.zbib.librarymanagement.dto.PatronUpdateRequest;
import dev.zbib.librarymanagement.entity.Patron;
import dev.zbib.librarymanagement.exception.PatronException;
import dev.zbib.librarymanagement.repository.PatronRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatronServiceTest {

    @Mock
    private PatronRepository patronRepository;

    @InjectMocks
    private PatronService patronService;

    private Patron testPatron;
    private PatronCreationRequest creationRequest;
    private PatronUpdateRequest updateRequest;
    private UUID patronId;

    @BeforeEach
    void setUp() {
        patronId = UUID.randomUUID();
        testPatron = Patron.builder()
                .id(patronId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phoneNumber("1234567890")
                .address("123 Main St")
                .membershipExpiryDate(LocalDateTime.now().plusYears(1))
                .build();

        creationRequest = PatronCreationRequest.builder()
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phoneNumber("0987654321")
                .address("456 Oak St")
                .membershipExpiryDate(LocalDateTime.now().plusYears(1))
                .build();

        updateRequest = PatronUpdateRequest.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated.name@example.com")
                .phoneNumber("1111111111")
                .address("789 Pine St")
                .membershipExpiryDate(LocalDateTime.now().plusYears(2))
                .build();
    }

    @Test
    void createPatron_ShouldReturnPatronId() {
        when(patronRepository.save(any(Patron.class))).thenReturn(testPatron);

        UUID result = patronService.createPatron(creationRequest);

        assertNotNull(result);
        assertEquals(testPatron.getId(), result);
        verify(patronRepository, times(1)).save(any(Patron.class));
    }

    @Test
    void getPatronById_WhenPatronExists_ShouldReturnPatron() {
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(testPatron));

        Patron result = patronService.getPatronById(patronId);

        assertNotNull(result);
        assertEquals(testPatron.getId(), result.getId());
        assertEquals(testPatron.getFirstName(), result.getFirstName());
        verify(patronRepository, times(1)).findById(patronId);
    }

    @Test
    void getPatronById_WhenPatronDoesNotExist_ShouldThrowException() {
        when(patronRepository.findById(patronId)).thenReturn(Optional.empty());

        assertThrows(PatronException.PatronNotFound.class, () ->
            patronService.getPatronById(patronId)
        );
        verify(patronRepository, times(1)).findById(patronId);
    }

    @Test
    void getPatronRequestById_WhenPatronExists_ShouldReturnPatronResponse() {
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(testPatron));

        PatronResponse response = patronService.getPatronRequestById(patronId);

        assertNotNull(response);
        assertEquals(testPatron.getId(), response.getId());
        assertEquals(testPatron.getFirstName(), response.getFirstName());
        assertEquals(testPatron.getLastName(), response.getLastName());
        verify(patronRepository, times(1)).findById(patronId);
    }

    @Test
    void getPatrons_ShouldReturnPageOfPatronResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Patron> patronPage = new PageImpl<>(List.of(testPatron));

        when(patronRepository.findAll(pageable)).thenReturn(patronPage);

        Page<PatronResponse> result = patronService.getPatrons(pageable);

        assertNotNull(result);
        assertFalse(result.getContent().isEmpty());
        assertEquals(1, result.getContent().size());
        verify(patronRepository, times(1)).findAll(pageable);
    }

    @Test
    void updatePatron_WhenPatronExists_ShouldReturnUpdatedPatronId() {
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(testPatron));
        when(patronRepository.save(any(Patron.class))).thenReturn(testPatron);

        UUID result = patronService.updatePatron(patronId, updateRequest);

        assertNotNull(result);
        assertEquals(patronId, result);
        verify(patronRepository, times(1)).findById(patronId);
        verify(patronRepository, times(1)).save(any(Patron.class));
    }

    @Test
    void updatePatron_WhenPatronDoesNotExist_ShouldThrowException() {
        when(patronRepository.findById(patronId)).thenReturn(Optional.empty());

        assertThrows(PatronException.PatronNotFound.class, () ->
            patronService.updatePatron(patronId, updateRequest)
        );
        verify(patronRepository, times(1)).findById(patronId);
        verify(patronRepository, never()).save(any(Patron.class));
    }

    @Test
    void updatePatron_ShouldUpdateOnlyProvidedFields() {
        when(patronRepository.findById(patronId)).thenReturn(Optional.of(testPatron));
        
        PatronUpdateRequest partialUpdate = PatronUpdateRequest.builder()
                .firstName("NewFirstName")
                .build();

        when(patronRepository.save(any(Patron.class))).thenAnswer(invocation -> {
            Patron savedPatron = (Patron) invocation.getArgument(0);
            assertEquals("NewFirstName", savedPatron.getFirstName());
            assertEquals(testPatron.getLastName(), savedPatron.getLastName());
            assertEquals(testPatron.getEmail(), savedPatron.getEmail());
            assertEquals(testPatron.getPhoneNumber(), savedPatron.getPhoneNumber());
            assertEquals(testPatron.getAddress(), savedPatron.getAddress());
            assertEquals(testPatron.getMembershipExpiryDate(), savedPatron.getMembershipExpiryDate());
            return savedPatron;
        });

        UUID result = patronService.updatePatron(patronId, partialUpdate);

        assertNotNull(result);
        assertEquals(patronId, result);
        verify(patronRepository, times(1)).save(any(Patron.class));
    }

    @Test
    void deletePatron_WhenPatronExists_ShouldDeleteSuccessfully() {
        when(patronRepository.existsById(patronId)).thenReturn(true);

        assertDoesNotThrow(() -> patronService.deletePatron(patronId));

        verify(patronRepository, times(1)).existsById(patronId);
        verify(patronRepository, times(1)).deleteById(patronId);
    }

    @Test
    void deletePatron_WhenPatronDoesNotExist_ShouldThrowException() {
        when(patronRepository.existsById(patronId)).thenReturn(false);

        assertThrows(PatronException.PatronNotFound.class, () ->
            patronService.deletePatron(patronId)
        );
        
        verify(patronRepository, times(1)).existsById(patronId);
        verify(patronRepository, never()).deleteById(any());
    }
} 