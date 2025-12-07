package com.meditrack.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository-Schnittstelle für den Datenbankzugriff.
 *
 * Spring Data JPA generiert automatisch die CRUD-Methoden.
 */
public interface JpaUserRepository extends JpaRepository<UserEntityJpa, String> {
}
