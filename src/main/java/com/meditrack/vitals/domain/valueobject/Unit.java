package com.meditrack.vitals.domain.valueobject;

/**
 * Einheit eines Messwertes.
 */
public enum Unit {
    MMHG("mmHg"),
    BPM("bpm"),
    CELSIUS("°C"),
    PERCENT("%");

    private final String label;

    Unit(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
