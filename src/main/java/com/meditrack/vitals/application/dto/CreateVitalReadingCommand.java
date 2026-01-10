package com.meditrack.vitals.application.dto;

import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;

import java.time.LocalDateTime;

/**
 * Command-DTO zum Anlegen eines Vitalwerts.
 */
public class CreateVitalReadingCommand {

    private String patientId;
    private VitalType type;
    private Double value;
    private Unit unit;
    private LocalDateTime measuredAt;

    public CreateVitalReadingCommand() {
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public VitalType getType() {
        return type;
    }

    public void setType(VitalType type) {
        this.type = type;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }
}
