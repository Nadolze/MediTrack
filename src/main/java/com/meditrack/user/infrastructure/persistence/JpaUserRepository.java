package com.meditrack.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA Repository für Benutzer.
 *
 * Diese Schnittstelle wird von Spring automatisch implementiert.
 * Sie stellt Standard-CRUD-Operationen bereit und
 * zusätzlich zwei Such-Methoden:
 *
 * - findByEmail(...)  → Benutzer über E-Mail-Adresse finden
 * - findByName(...)   → Benutzer über Namen finden
 */
public interface JpaUserRepository extends JpaRepository<UserEntityJpa, String> {

    /**
     * Sucht einen Benutzer anhand seiner E-Mail-Adresse.
     */
    Optional<UserEntityJpa> findByEmail(String email);

    /**
     * Sucht einen Benutzer anhand seines Namens.
     */
    Optional<UserEntityJpa> findByName(String name);
}
