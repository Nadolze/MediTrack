package com.meditrack.alerts.domain.service;

import com.meditrack.alerts.domain.valueobject.AlertMessage;
import com.meditrack.alerts.domain.valueobject.Severity;
import com.meditrack.vitals.domain.service.ThresholdCatalog;
import com.meditrack.vitals.domain.valueobject.Threshold;
import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;

import java.util.Optional;

/**
 * Fachliche Bewertung: Wird aus einem VitalReading ein Alert?
 *
 * Hinweis:
 * - Regeln sind bewusst einfach gehalten (MVP).
 * - Die Grenzwerte kommen aus dem vitals-BC (ThresholdCatalog), weil es dieselben Fachwerte sind.
 */
public class AlertEvaluator {

    private final ThresholdCatalog thresholds;

    public AlertEvaluator(ThresholdCatalog thresholds) {
        this.thresholds = thresholds;
    }

    public Optional<EvaluationResult> evaluate(VitalType type, double value, Unit unit) {

        // O2 ist "kritisch" bei Unterschreitung -> Threshold nutzt MIN
        Threshold critical = thresholds.critical(type);
        Threshold warning = thresholds.warning(type);

        if (critical != null) {
            if (critical.isAbove(value) || critical.isBelow(value)) {
                return Optional.of(new EvaluationResult(
                        Severity.CRITICAL,
                        new AlertMessage(buildMessage(type, value, unit, Severity.CRITICAL))
                ));
            }
        }

        if (warning != null) {
            if (warning.isAbove(value) || warning.isBelow(value)) {
                return Optional.of(new EvaluationResult(
                        Severity.WARNING,
                        new AlertMessage(buildMessage(type, value, unit, Severity.WARNING))
                ));
            }
        }

        return Optional.empty();
    }

    private String buildMessage(VitalType type, double value, Unit unit, Severity severity) {
        return switch (severity) {
            case CRITICAL -> "KRITISCH: " + type + " = " + value + " " + unit.label();
            case WARNING -> "WARNUNG: " + type + " = " + value + " " + unit.label();
            default -> "INFO: " + type + " = " + value + " " + unit.label();
        };
    }

    public record EvaluationResult(Severity severity, AlertMessage message) {}
}
