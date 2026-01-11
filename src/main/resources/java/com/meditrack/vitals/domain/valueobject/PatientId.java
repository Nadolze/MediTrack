package com.meditrack.vitals.domain.valueobject;

import java.util.Objects;

/**
 * Referenz auf Patient (kommt aus coredata/user; hier nur als ID).
 */
public record PatientId(String value) {

    public PatientId {
        Objects.requireNonNull(value, "PatientId darf nicht null sein.");
        if (value.isBlank()) {
            throw new IllegalArgumentException("PatientId darf nicht leer sein.");
        }
    }
}
