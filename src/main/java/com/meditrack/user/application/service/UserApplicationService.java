package com.meditrack.user.application.service;

import com.meditrack.user.domain.entity.User;
import com.meditrack.user.domain.valueobject.UserId;
import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;
import org.springframework.stereotype.Service;

/**
 * Application Service für Benutzerfunktionen.
 *
 * Aufgaben:
 * ----------
 * - Orchestriert Anwendungsfälle (Use Cases) rund um Benutzer.
 * - Kommuniziert mit der Domain-Schicht (User, UserId).
 * - Übergibt Daten an die Infrastruktur für die Datenbank-Speicherung.
 * - Enthält selbst keine fachliche Logik, sondern steuert nur Abläufe.
 */
@Service
public class UserApplicationService {

    // Repository für die Datenbankinteraktion (Infrastruktur-Schicht)
    private final JpaUserRepository repository;

    /**
     * Konstruktor-Injektion durch Spring Boot.
     *
     * @param repository JPA-Repository zur Speicherung von Benutzern
     */
    public UserApplicationService(JpaUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Erstellt einen neuen Benutzer.
     *
     * Ablauf:
     * -------
     * 1. Domain-Objekt "User" erstellen (mit Validierung).
     * 2. Domain-Objekt in eine JPA-Entity umwandeln (Mapper).
     * 3. JPA-Entity speichern.
     * 4. Domain-Objekt zurückgeben.
     *
     * @param name  Name des Benutzers
     * @param email E-Mail des Benutzers
     * @return erzeugter Benutzer als Domain-Objekt
     */
    public User createUser(String name, String email) {

        // 1) Domain-Objekt erstellen (führt eigene Validierungen durch)
        User user = new User(UserId.generate(), name, email);

        // 2) Domain → JPA-Entity (Mapping)
        UserEntityJpa entity = new UserEntityJpa(
                user.getId().getValue(),
                user.getName(),
                user.getEmail()
        );

        // 3) Speichern in der Datenbank
        repository.save(entity);

        // 4) Domain-Objekt zurückgeben
        return user;
    }
}
