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
     *
     * Hintergrund:
     * - Value Objects werden oft über Fabrikmethoden instanziert.
     * - UUIDs sind ideal, um global eindeutige IDs zu erzeugen.
     *
     * @return neue UserId mit zufälligem UUID-Wert
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    /**
     * Gibt den internen ID-Wert zurück.
     */
    public String getValue() {
        return value;
    }
}
