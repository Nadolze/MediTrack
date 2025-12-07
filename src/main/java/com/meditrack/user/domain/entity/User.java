package com.meditrack.user.domain.entity;

import com.meditrack.user.domain.valueobject.UserId;

/**
 * Diese Klasse stellt die Benutzer-Entität im Domain-Layer dar.
 *
 * WICHTIG:
 * - Sie enthält ausschließlich fachliche Logik (kein JPA, kein Spring).
 * - Alle fachlichen Regeln zu Benutzern werden hier abgebildet.
 * - Persistenz (Datenbank) erfolgt in der Infrastructure-Schicht,
 *   niemals direkt hier.
 */
public class User {

    // Eindeutige Benutzer-ID als ValueObject (DDD: Identität)
    private final UserId id;

    // Benutzername (muss gültig und nicht leer sein)
    private String name;

    // E-Mail-Adresse des Benutzers (einfache fachliche Validierung)
    private String email;

    /**
     * Konstruktor der Benutzer-Entität.
     *
     * @param id    Eindeutige Benutzer-ID (ValueObject)
     * @param name  Benutzername
     * @param email E-Mail-Adresse
     *
     * Der Konstruktor prüft:
     * - ob die ID existiert
     * - ob der Name gültig ist
     * - ob die E-Mail eine einfache Strukturprüfung besteht
     */
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

    // Getter für die Benutzer-ID
    public UserId getId() {
        return id;
    }

    // Getter für den Benutzernamen
    public String getName() {
        return name;
    }

    // Getter für die E-Mail-Adresse
    public String getEmail() {
        return email;
    }

    /**
     * Fachliche Operation zum Ändern des Benutzernamens.
     *
     * Diese Methode spiegelt eine Regel aus der Domäne wider:
     * Ein Benutzername darf nie leer oder null sein.
     *
     * @param neuerName neuer Benutzername
     */
    public void changeName(String neuerName) {
        if (neuerName == null || neuerName.isBlank()) {
            throw new IllegalArgumentException("Neuer Name darf nicht leer sein.");
        }
        this.name = neuerName;
    }
}
