package com.meditrack.assignment.domain.valueobject;

/**
 * Patient Identifier (im aktuellen Stand: id aus mt_user / Session userId).
 */
public record PatientId(String value) {

    public PatientId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("PatientId darf nicht leer sein");
        }
    }
}
