package com.meditrack.alerts.domain.valueobject;

import java.util.Objects;

/**
 * Referenz auf Patient (nur ID).
 */
public record PatientId(String value) {
    public PatientId {
        Objects.requireNonNull(value, "PatientId darf nicht null sein.");
        if (value.isBlank()) throw new IllegalArgumentException("PatientId darf nicht leer sein.");
    }
}
