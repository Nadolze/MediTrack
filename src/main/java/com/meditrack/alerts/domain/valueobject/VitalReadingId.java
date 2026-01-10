package com.meditrack.alerts.domain.valueobject;

import java.util.Objects;

/**
 * Referenz auf VitalReading aus dem vitals-BC.
 */
public record VitalReadingId(String value) {
    public VitalReadingId {
        Objects.requireNonNull(value, "VitalReadingId darf nicht null sein.");
        if (value.isBlank()) throw new IllegalArgumentException("VitalReadingId darf nicht leer sein.");
    }
}
