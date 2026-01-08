package com.meditrack.user.domain.entity;

import com.meditrack.user.domain.valueobject.UserId;
import com.meditrack.user.domain.valueobject.UserRole;

/**
 * Domänen-Entität für einen Benutzer.
 *
 * DDD:
 * - User-BC ist Owner für Identität & Rollen.
 * - Passwort liegt nicht in der Domain, sondern in der Infrastruktur (Hash).
 */
public class User {

    private final UserId id;
    private String name;
    private String email;
    private UserRole role;

    public User(UserId id, String name, String email) {
        this(id, name, email, UserRole.PATIENT);
    }

    public User(UserId id, String name, String email, UserRole role) {
        if (id == null) {
            throw new IllegalArgumentException("UserId darf nicht null sein.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name darf nicht leer sein.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("E-Mail ist ungültig.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Rolle darf nicht null sein.");
        }

        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public UserId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void changeName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Neuer Name darf nicht leer sein.");
        }
        this.name = newName;
    }

    public String getEmail() {
        return email;
    }

    public void changeEmail(String newEmail) {
        if (newEmail == null || !newEmail.contains("@")) {
            throw new IllegalArgumentException("Neue E-Mail ist ungültig.");
        }
        this.email = newEmail;
    }

    public UserRole getRole() {
        return role;
    }

    public void changeRole(UserRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("Neue Rolle darf nicht null sein.");
        }
        this.role = newRole;
    }
}
