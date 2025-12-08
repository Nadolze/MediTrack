package com.meditrack.user.application.service;

import com.meditrack.user.application.dto.UserRegistrationDto;
import com.meditrack.user.domain.entity.User;
import com.meditrack.user.domain.valueobject.UserId;
import com.meditrack.user.infrastructure.persistence.JpaUserRepository;
import com.meditrack.user.infrastructure.persistence.UserEntityJpa;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application Service für Benutzerfunktionen.
 *
 * Orchestriert Anwendungsfälle (Use Cases) rund um Benutzer.
 */
@Service
public class UserApplicationService {

    /**
     * JPA-Repository für die Benutzer-Entität.
     */
    private final JpaUserRepository repository;

    /**
     * Konstruktor-Injektion durch Spring.
     */
    public UserApplicationService(JpaUserRepository repository) {
        this.repository = repository;
    }

    /**
     * Registriert einen neuen Benutzer auf Basis des Formular-DTOs.
     *
     * Aktuell:
     * - Passwort wird als Plain-Text gespeichert (TODO: Verschlüsselung hinzufügen).
     * - Name im Domain-Modell entspricht dem Benutzernamen.
     */
    public User registerUser(UserRegistrationDto dto) {
        // 1) Domain-Objekt erstellen
        User user = new User(
                UserId.generate(),
                dto.getUsername(),
                dto.getEmail()
        );

        // 2) Domain → JPA-Entität (4 Argumente: id, name, email, password)
        UserEntityJpa entity = new UserEntityJpa(
                user.getName(),
                user.getEmail(),
                dto.getPassword()
        );

        // 3) Speichern
        repository.save(entity);

        // 4) Domain-Objekt zurückgeben
        return user;
    }

    /**
     * Einfache Login-Prüfung.
     *
     * Derzeit:
     * - Es wird nur geprüft, ob ein Benutzer mit diesem Namen
     *   oder dieser E-Mail existiert.
     * - Das Passwort wird noch ignoriert (Platzhalter für spätere Erweiterung).
     */
    public boolean login(String usernameOrEmail, String password) {
        Optional<UserEntityJpa> userOpt =
                repository.findByEmail(usernameOrEmail)
                        .or(() -> repository.findByName(usernameOrEmail));

        return userOpt.isPresent();
    }
}