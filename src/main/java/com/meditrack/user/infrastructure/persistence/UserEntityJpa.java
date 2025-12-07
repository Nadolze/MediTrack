package com.meditrack.user.infrastructure.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA-Entity für die Datenbanktabelle "users".
 *
 * Diese Klasse wird in der Infrastructure-Schicht eingesetzt,
 * um den Benutzer in der Datenbank zu speichern.
 */
@Entity
@Table(name = "users")
public class UserEntityJpa {

    @Id
    private String id;

    private String name;
    private String email;

    // Standardkonstruktor für JPA
    protected UserEntityJpa() {
    }

    // Konstruktor für den ApplicationService
    public UserEntityJpa(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getter für JPA
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
