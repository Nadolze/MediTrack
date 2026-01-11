package com.meditrack.vitals.domain.entity;

import com.meditrack.vitals.domain.valueobject.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Ein einzelner Vitalwert eines Patienten zu einem bestimmten Zeitpunkt.
 *
 * DDD:
 * - Aggregate Root im BC vitals.
 * - Andere BCs referenzieren VitalReading nur über ID.
 */
@Entity
@Table(name = "mt_vital_reading")
public class VitalReading {

    @Id
    @Column(name = "id", length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "patient_id", length = 36, nullable = false)
    private String patientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "vital_type", length = 50, nullable = false)
    private VitalType type;

    /**
     * Achtung: NICHT "value" als Spaltenname verwenden (H2 Keyword).
     */
    // Hinweis: Bei SQL-Floating-Point-Typen (double/float) hat "scale" keine Bedeutung.
    // Hibernate 6 wirft sonst beim Start einen Fehler (u. a. in DataJpaTest / H2).
    @Column(name = "value_numeric", nullable = false)
    private double value;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", length = 20, nullable = false)
    private Unit unit;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "recorded_by_staff_id", length = 36)
    private String recordedByStaffId;

    protected VitalReading() {
        // JPA
    }

    private VitalReading(String id,
                         String patientId,
                         VitalType type,
                         double value,
                         Unit unit,
                         LocalDateTime measuredAt,
                         String recordedByStaffId) {

        this.id = Objects.requireNonNull(id, "id darf nicht null sein.");
        this.patientId = new PatientId(patientId).value();
        this.type = Objects.requireNonNull(type, "type darf nicht null sein.");
        this.value = new MeasurementValue(value).value();
        this.unit = Objects.requireNonNull(unit, "unit darf nicht null sein.");
        this.measuredAt = Objects.requireNonNull(measuredAt, "measuredAt darf nicht null sein.");
        this.recordedByStaffId = (recordedByStaffId == null || recordedByStaffId.isBlank()) ? null : recordedByStaffId;
    }

    public static VitalReading create(PatientId patientId,
                                      VitalType type,
                                      MeasurementValue value,
                                      Unit unit,
                                      LocalDateTime measuredAt,
                                      String recordedByStaffId) {

        return new VitalReading(
                VitalReadingId.newId().value(),
                patientId.value(),
                type,
                value.value(),
                unit,
                measuredAt,
                recordedByStaffId
        );
    }

    public VitalReadingId getVitalReadingId() {
        return new VitalReadingId(id);
    }

    public PatientId getPatientId() {
        return new PatientId(patientId);
    }

    public VitalType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public Unit getUnit() {
        return unit;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public String getRecordedByStaffId() {
        return recordedByStaffId;
    }
}
