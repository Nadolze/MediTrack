package com.meditrack.vitals.domain.valueobject;

public record MeasurementValue(double value) {

    public MeasurementValue {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Messwert muss eine reale Zahl sein (war: " + value + ").");
        }
    }
}
