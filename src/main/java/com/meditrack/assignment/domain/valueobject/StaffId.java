package com.meditrack.assignment.domain.valueobject;

/**
 * Identifier für medizinisches Personal.
 *
 * Technischer Hinweis:
 * - In der DB ist mt_medical_staff.id der FK-Partner (z.B. vitals.recorded_by_staff_id).
 */
public record StaffId(String value) {

    public StaffId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("StaffId darf nicht leer sein");
        }
    }
}
