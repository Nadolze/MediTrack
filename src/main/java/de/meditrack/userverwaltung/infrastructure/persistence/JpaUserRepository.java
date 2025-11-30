package de.meditrack.userverwaltung.infrastructure.persistence;

import de.meditrack.userverwaltung.domain.model.User;
import de.meditrack.userverwaltung.domain.repositories.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository
        extends JpaRepository<User, Long>, UserRepository {

    // KEINE weiteren Methoden nötig!
    // Spring Data erzeugt:
    // - findByEmail
    // - existsByEmail
    // - findByUsername
    // - existsByUsername
}
