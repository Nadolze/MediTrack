package com.meditrack.alerts.domain.service;

import com.meditrack.alerts.domain.valueobject.Severity;
import com.meditrack.vitals.domain.service.ThresholdCatalog;
import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests für den AlertEvaluator.
 *
 * Ziel:
 * - Validierung der fachlichen Bewertungslogik für Vitalwerte
 * - Sicherstellen der korrekten Severity-Einstufung anhand von Grenzwerten
 *
 */
class AlertEvaluatorTest {

    @Test
    void returnsCriticalForHighSystolic() {
        AlertEvaluator eval = new AlertEvaluator(new ThresholdCatalog());

        var result = eval.evaluate(VitalType.BLOOD_PRESSURE_SYSTOLIC, 190, Unit.MMHG);

        assertThat(result).isPresent();
        assertThat(result.get().severity()).isEqualTo(Severity.CRITICAL);
    }

    @Test
    void returnsWarningForFever() {
        AlertEvaluator eval = new AlertEvaluator(new ThresholdCatalog());

        var result = eval.evaluate(VitalType.TEMPERATURE, 39.0, Unit.CELSIUS);

        assertThat(result).isPresent();
        assertThat(result.get().severity()).isEqualTo(Severity.WARNING);
    }

    @Test
    void returnsEmptyForNormalValue() {
        AlertEvaluator eval = new AlertEvaluator(new ThresholdCatalog());

        var result = eval.evaluate(VitalType.PULSE, 70, Unit.BPM);

        assertThat(result).isEmpty();
    }

    @Test
    void returnsCriticalForLowOxygen() {
        AlertEvaluator eval = new AlertEvaluator(new ThresholdCatalog());

        var result = eval.evaluate(VitalType.OXYGEN_SATURATION, 85, Unit.PERCENT);

        assertThat(result).isPresent();
        assertThat(result.get().severity()).isEqualTo(Severity.CRITICAL);
    }
}
