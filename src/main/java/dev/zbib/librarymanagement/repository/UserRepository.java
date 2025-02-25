package dev.zbib.librarymanagement.repository;

import dev.zbib.librarymanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String username);

    boolean existsByEmail(String email);
}
