package com.meditrack.vitals.domain.valueobject;

/**
 * Typ des Vitalwerts.
 *
 * Hinweis:
 * - Blutdruck wird getrennt erfasst (systolisch/diastolisch).
 */
public enum VitalType {
    BLOOD_PRESSURE_SYSTOLIC,
    BLOOD_PRESSURE_DIASTOLIC,
    PULSE,
    TEMPERATURE,
    OXYGEN_SATURATION
}
