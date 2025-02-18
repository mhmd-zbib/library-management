package dev.zbib.librarymanagement.service;

import dev.zbib.librarymanagement.builder.PatronBuilder;
import dev.zbib.librarymanagement.dto.PatronCreationRequest;
import dev.zbib.librarymanagement.dto.PatronResponse;
import dev.zbib.librarymanagement.dto.PatronUpdateRequest;
import dev.zbib.librarymanagement.entity.Patron;
import dev.zbib.librarymanagement.exception.PatronException;
import dev.zbib.librarymanagement.repository.PatronRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static dev.zbib.librarymanagement.builder.PatronBuilder.buildPatron;
import static dev.zbib.librarymanagement.builder.PatronBuilder.buildPatronResponse;

@Service
@RequiredArgsConstructor
public class PatronService {

    private final PatronRepository patronRepository;

    @Transactional
    public UUID createPatron(PatronCreationRequest request) {
        Patron patron = buildPatron(request);
        patron = patronRepository.save(patron);
        return patron.getId();
    }

    public PatronResponse getPatronRequestById(UUID id) {
        Patron patron = getPatronById(id);
        return buildPatronResponse(patron);
    }

    public Patron getPatronById(UUID id) {
        return patronRepository.findById(id)
                .orElseThrow(PatronException.PatronNotFound::new);
    }

    public Page<PatronResponse> getPatrons(Pageable pageable) {
        return patronRepository.findAll(pageable)
                .map(PatronBuilder::buildPatronResponse);
    }

    @Transactional
    public void deletePatron(UUID id) {
        if (!patronRepository.existsById(id)) {
            throw new PatronException.PatronNotFound();
        }
        patronRepository.deleteById(id);
    }

    @Transactional
    public UUID updatePatron(UUID id, PatronUpdateRequest request) {
        Patron patron = patronRepository.findById(id)
                .orElseThrow(PatronException.PatronNotFound::new);

        if (request.getFirstName() != null) {
            patron.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            patron.setLastName(request.getLastName());
        }
        if (request.getEmail() != null) {
            patron.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            patron.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            patron.setAddress(request.getAddress());
        }
        if (request.getMembershipExpiryDate() != null) {
            patron.setMembershipExpiryDate(request.getMembershipExpiryDate());
        }

        patron = patronRepository.save(patron);
        return patron.getId();
    }
}