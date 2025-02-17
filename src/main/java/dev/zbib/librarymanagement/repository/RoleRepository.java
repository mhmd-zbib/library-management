package dev.zbib.librarymanagement.repository;

import dev.zbib.librarymanagement.entity.Role;
import dev.zbib.librarymanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByName(String name);
}
