package com.meditrack.user.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * JPA-Entität für Benutzer.
 *
 * Diese Klasse spiegelt die Tabelle "users" in der Datenbank wider.
 */
@Entity
@Table(name = "users")
public class UserEntityJpa {

    /**
     * Primärschlüssel des Benutzers (auto-generated).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Anzeigename des Benutzers.
     */
    private String name;

    /**
     * E-Mail-Adresse des Benutzers.
     */
    @Column(unique = true)
    private String email;

    /**
     * Passwort des Benutzers.
     */
    private String password;

    /**
     * Standard-Konstruktor nur für JPA.
     */
    protected UserEntityJpa() {
    }

    /**
     * Konstruktor zum Erzeugen einer neuen User-Entität im Anwendungscode.
     *
     * @param name     Name des Benutzers
     * @param email    E-Mail-Adresse des Benutzers
     * @param password Passwort des Benutzers
     */
    public UserEntityJpa(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // --- Getter ---

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}