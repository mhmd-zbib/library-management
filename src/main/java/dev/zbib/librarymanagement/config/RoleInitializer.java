package dev.zbib.librarymanagement.config;

import dev.zbib.librarymanagement.dto.Roles;
import dev.zbib.librarymanagement.entity.Role;
import dev.zbib.librarymanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Log4j2
public class RoleInitializer {

    private final RoleRepository roleRepository;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public CommandLineRunner initializeRoles() {
        return args -> {
            log.info("Initializing roles...");

            List<String> roleNames = Arrays.asList(Roles.PATRON.name(),
                    Roles.LIBRARIAN.name());

            for (String roleName : roleNames) {
                if (roleRepository.findByName(roleName)
                        .isEmpty()) {
                    Role role = Role.builder()
                            .name(roleName)
                            .build();

                    roleRepository.save(role);
                    log.info("Created role: {}",
                            roleName);
                } else {
                    log.info("Role already exists: {}",
                            roleName);
                }
            }

            log.info("Roles initialization completed");
        };
    }
}
