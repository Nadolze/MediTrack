package com.meditrack.user.domain.entity;

import com.meditrack.user.domain.valueobject.UserId;

/**
 * Domänen-Entität für einen Benutzer.
 *
 * Vereinfachte Version:
 * - Benutzer hat eine ID, einen Namen und eine E-Mail-Adresse.
 * - Passwort wird aktuell nicht in der Domäne modelliert.
 */
public class User {

    private final UserId id;
    private String name;
    private String email;

    public User(UserId id, String name, String email) {
        if (id == null) {
            throw new IllegalArgumentException("UserId darf nicht null sein.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name darf nicht leer sein.");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("E-Mail ist ungültig.");
        }

        this.id = id;
        this.name = name;
        this.email = email;
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
}
