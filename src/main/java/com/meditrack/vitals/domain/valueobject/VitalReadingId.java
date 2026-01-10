package com.meditrack.vitals.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Eindeutige Kennung eines Vitalwert-Eintrags.
 */
public record VitalReadingId(String value) {

    public VitalReadingId {
        Objects.requireNonNull(value, "VitalReadingId darf nicht null sein.");
        if (value.isBlank()) {
            throw new IllegalArgumentException("VitalReadingId darf nicht leer sein.");
        }
    }

    public static VitalReadingId newId() {
        return new VitalReadingId(UUID.randomUUID().toString());
    }
}
