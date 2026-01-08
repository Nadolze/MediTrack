package com.meditrack.user.domain.valueobject;

import java.util.UUID;

/**
 * Value Object für die eindeutige Benutzer-ID.
 */
public class UserId {

    private final String value;

    /**
     * Erzeugt eine neue Benutzer-ID aus einem übergebenen Wert.
     */
    public UserId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("UserId darf nicht leer sein.");
        }
        this.value = value;
    }

    /**
     * Fabrikmethode zur automatischen Erzeugung einer neuen eindeutigen Benutzer-ID.
     * Generiert eine kürzere ID (8 Zeichen) für Kompatibilität mit bestehender DB.
     */
    public static UserId generate() {
        // Nutze nur die ersten 8 Zeichen der UUID (ohne Bindestriche)
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return new UserId(uuid.substring(0, 8));
    }

    /**
     * Gibt den internen ID-Wert zurück.
     */
    public String getValue() {
        return value;
    }
}