package com.meditrack.vitals.domain.valueobject;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

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
