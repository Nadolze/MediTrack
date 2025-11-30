package de.meditrack.userverwaltung.infrastructure.persistence;

import de.meditrack.userverwaltung.domain.repositories.UserRepository;
import de.meditrack.userverwaltung.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends UserRepository, JpaRepository<User, Long> {

    @Override
    Optional<User> findByUsername(String username);

    @Override
    Optional<User> findByEmail(String email);
}
