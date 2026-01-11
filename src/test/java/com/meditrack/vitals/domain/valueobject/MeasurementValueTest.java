package com.meditrack.vitals.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
/**
 * Unit-Test für die Domänen-ValueObject-Klasse MeasurementValue.
 *
 * Ziel:
 * - Sicherstellen, dass nur endliche Zahlen akzeptiert werden.
 * - NaN oder Infinity sollen eine IllegalArgumentException auslösen.
 * - Finite Zahlen sollen korrekt gespeichert werden.
 */
class MeasurementValueTest {

    @Test
    void constructor_shouldRejectNaN() {
        assertThatThrownBy(() -> new MeasurementValue(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reale Zahl")
                .hasMessageContaining("NaN");
    }

    @Test
    void constructor_shouldRejectInfinity() {
        assertThatThrownBy(() -> new MeasurementValue(Double.POSITIVE_INFINITY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("reale Zahl")
                .hasMessageContaining("Infinity");
    }

    @Test
    void constructor_shouldAcceptFinite() {
        var mv = new MeasurementValue(42.0);
        assertThat(mv.value()).isEqualTo(42.0);
    }
}
