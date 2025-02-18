package dev.zbib.librarymanagement.builder;

import dev.zbib.librarymanagement.dto.PatronRequest;
import dev.zbib.librarymanagement.dto.PatronResponse;
import dev.zbib.librarymanagement.entity.Patron;
import org.springframework.stereotype.Component;

@Component
public class PatronBuilder {

    public static Patron buildPatron(PatronRequest request) {
        return Patron.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .membershipStatus(request.getMembershipStatus())
                .membershipExpiryDate(request.getMembershipExpiryDate())
                .build();
    }

    public static PatronResponse buildPatronResponse(Patron patron) {
        return PatronResponse.builder()
                .id(patron.getId())
                .firstName(patron.getFirstName())
                .lastName(patron.getLastName())
                .email(patron.getEmail())
                .phoneNumber(patron.getPhoneNumber())
                .address(patron.getAddress())
                .membershipStatus(patron.getMembershipStatus())
                .membershipExpiryDate(patron.getMembershipExpiryDate())
                .build();
    }
}