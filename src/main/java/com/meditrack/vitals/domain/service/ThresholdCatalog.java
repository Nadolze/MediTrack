package com.meditrack.vitals.domain.service;

import com.meditrack.vitals.domain.valueobject.Threshold;
import com.meditrack.vitals.domain.valueobject.VitalType;

import java.util.EnumMap;
import java.util.Map;

public class ThresholdCatalog {

    private static final Threshold EMPTY = Threshold.minMax(null, null);

    private final Map<VitalType, Threshold> warningUpper = new EnumMap<>(VitalType.class);
    private final Map<VitalType, Threshold> criticalUpper = new EnumMap<>(VitalType.class);

    public ThresholdCatalog() {
        // WARNING
        warningUpper.put(VitalType.BLOOD_PRESSURE_SYSTOLIC, Threshold.minMax(90.0, 160.0));
        warningUpper.put(VitalType.BLOOD_PRESSURE_DIASTOLIC, Threshold.minMax(60.0, 100.0));
        warningUpper.put(VitalType.PULSE, Threshold.minMax(50.0, 110.0));
        warningUpper.put(VitalType.TEMPERATURE, Threshold.minMax(35.0, 38.5));
        warningUpper.put(VitalType.OXYGEN_SATURATION, Threshold.minMax(92.0, null)); // MIN wichtig

        // CRITICAL
        criticalUpper.put(VitalType.BLOOD_PRESSURE_SYSTOLIC, Threshold.minMax(80.0, 180.0));
        criticalUpper.put(VitalType.BLOOD_PRESSURE_DIASTOLIC, Threshold.minMax(50.0, 120.0));
        criticalUpper.put(VitalType.PULSE, Threshold.minMax(40.0, 130.0));
        criticalUpper.put(VitalType.TEMPERATURE, Threshold.minMax(32.0, 39.5));
        criticalUpper.put(VitalType.OXYGEN_SATURATION, Threshold.minMax(88.0, null)); // MIN wichtig
    }

    /**
     * WARNING-Schwellenwerte (gelb).
     * Gibt nie null zurück (bei null/fehlendem Typ -> EMPTY).
     */
    public Threshold warning(VitalType type) {
        if (type == null) return EMPTY;
        return warningUpper.getOrDefault(type, EMPTY);
    }

    /**
     * CRITICAL-Schwellenwerte (rot).
     * Gibt nie null zurück (bei null/fehlendem Typ -> EMPTY).
     */
    public Threshold critical(VitalType type) {
        if (type == null) return EMPTY;
        return criticalUpper.getOrDefault(type, EMPTY);
    }
}
