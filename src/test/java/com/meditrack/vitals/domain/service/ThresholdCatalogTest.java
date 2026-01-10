package com.meditrack.vitals.domain.service;

import com.meditrack.vitals.domain.valueobject.Threshold;
import com.meditrack.vitals.domain.valueobject.VitalType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ThresholdCatalogTest {

    @Test
    void warning_shouldReturnConfiguredThresholds() {
        ThresholdCatalog catalog = new ThresholdCatalog();

        var pulse = catalog.warning(VitalType.PULSE);
        assertThat(pulse.minInclusive()).isNull();
        assertThat(pulse.maxInclusive()).isEqualTo(110.0);

        var sys = catalog.warning(VitalType.BLOOD_PRESSURE_SYSTOLIC);
        assertThat(sys.maxInclusive()).isEqualTo(160.0);

        var dia = catalog.warning(VitalType.BLOOD_PRESSURE_DIASTOLIC);
        assertThat(dia.maxInclusive()).isEqualTo(100.0);

        var oxy = catalog.warning(VitalType.OXYGEN_SATURATION);
        assertThat(oxy.minInclusive()).isEqualTo(92.0);
        assertThat(oxy.maxInclusive()).isNull();
    }

    @Test
    void critical_shouldReturnConfiguredThresholds() {
        ThresholdCatalog catalog = new ThresholdCatalog();

        var pulse = catalog.critical(VitalType.PULSE);
        assertThat(pulse.maxInclusive()).isEqualTo(130.0);

        var sys = catalog.critical(VitalType.BLOOD_PRESSURE_SYSTOLIC);
        assertThat(sys.maxInclusive()).isEqualTo(180.0);

        var dia = catalog.critical(VitalType.BLOOD_PRESSURE_DIASTOLIC);
        assertThat(dia.maxInclusive()).isEqualTo(120.0);

        var oxy = catalog.critical(VitalType.OXYGEN_SATURATION);
        assertThat(oxy.minInclusive()).isEqualTo(88.0);
        assertThat(oxy.maxInclusive()).isNull();
    }

    @Test
    void nullVitalType_returnsEmptyThreshold() {
        ThresholdCatalog catalog = new ThresholdCatalog();

        Threshold w = catalog.warning(null);
        Threshold c = catalog.critical(null);

        assertThat(w).isNotNull();
        assertThat(w.minInclusive()).isNull();
        assertThat(w.maxInclusive()).isNull();

        assertThat(c).isNotNull();
        assertThat(c.minInclusive()).isNull();
        assertThat(c.maxInclusive()).isNull();
    }
}
