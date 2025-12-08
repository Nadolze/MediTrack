package com.meditrack.user.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA-Entität für Benutzer.
 *
 * Diese Klasse spiegelt die Tabelle "users" in der Datenbank wider.
 * Aktuell werden nur die Felder id, name und email gespeichert.
 */
@Entity
@Table(name = "users")
public class UserEntityJpa {

    /**
     * Primärschlüssel des Benutzers (z.B. UUID als String).
     */
    @Id
    private String id;

    /**
     * Anzeigename des Benutzers.
     */
    private String name;

    /**
     * E-Mail-Adresse des Benutzers.
     */
    private String email;

    /**
     * Standard-Konstruktor nur für JPA.
     */
    protected UserEntityJpa() {
    }

    /**
     * Konstruktor zum Erzeugen einer neuen User-Entität im Anwendungscode.
     *
     * @param id    ID des Benutzers
     * @param name  Name des Benutzers
     * @param email E-Mail-Adresse des Benutzers
     */
    public UserEntityJpa(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // --- Getter ---

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
