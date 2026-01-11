package com.meditrack.vitals.domain.valueobject;

import java.util.Objects;

/**
 * Grenzwerte für die Bewertung (nicht zwingend = Validierung).
 */
public final class Threshold {

    private final Double minInclusive;
    private final Double maxInclusive;

    public Threshold(Double minInclusive, Double maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
        if (minInclusive != null && maxInclusive != null && minInclusive > maxInclusive) {
            throw new IllegalArgumentException("minInclusive darf nicht größer als maxInclusive sein.");
        }
    }

    public static Threshold minMax(Double minInclusive, Double maxInclusive) {
        return new Threshold(minInclusive, maxInclusive);
    }

    public boolean isBelow(double value) {
        return minInclusive != null && value < minInclusive;
    }

    public boolean isAbove(double value) {
        return maxInclusive != null && value > maxInclusive;
    }

    public Double minInclusive() {
        return minInclusive;
    }

    public Double maxInclusive() {
        return maxInclusive;
    }
}
