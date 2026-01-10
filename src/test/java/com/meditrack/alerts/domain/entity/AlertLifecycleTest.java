package com.meditrack.alerts.domain.entity;

import com.meditrack.alerts.domain.valueobject.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit-Tests für den Alert-Lebenszyklus (DDD Aggregate).
 */
class AlertLifecycleTest {

    @Test
    @DisplayName("trigger erzeugt einen OPEN-Alert mit korrekten Feldern")
    void trigger_shouldCreateOpenAlertWithIdAndTimestamps() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 9, 21, 0);

        Alert alert = Alert.trigger(
                AlertId.newId(),
                new PatientId("p1"),
                new VitalReadingId("r1"),
                Severity.CRITICAL,
                new AlertMessage("KRITISCH: PULSE = 200.0 BPM"),
                now
        );

        assertThat(alert.getId()).isNotBlank();
        assertThat(alert.getPatientId()).isEqualTo("p1");
        assertThat(alert.getVitalReadingId()).isEqualTo("r1");
        assertThat(alert.getSeverity()).isEqualTo(Severity.CRITICAL);
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.OPEN);
        assertThat(alert.getCreatedAt()).isEqualTo(now);
        assertThat(alert.getResolvedAt()).isNull();
    }

    @Test
    @DisplayName("acknowledge setzt Status auf ACKNOWLEDGED (wenn nicht RESOLVED)")
    void acknowledge_shouldSetStatusAcknowledged() {
        Alert alert = Alert.trigger(
                AlertId.newId(),
                new PatientId("p1"),
                new VitalReadingId("r1"),
                Severity.WARNING,
                new AlertMessage("WARNUNG: TEMP = 39.2 C"),
                LocalDateTime.of(2026, 1, 9, 21, 0)
        );

        alert.acknowledge();

        assertThat(alert.getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
        assertThat(alert.getResolvedAt()).isNull();
    }

    @Test
    @DisplayName("resolve setzt Status auf RESOLVED und resolvedAt")
    void resolve_shouldSetStatusResolvedAndResolvedAt() {
        Alert alert = Alert.trigger(
                AlertId.newId(),
                new PatientId("p1"),
                new VitalReadingId("r1"),
                Severity.CRITICAL,
                new AlertMessage("KRITISCH: PULSE = 210.0 BPM"),
                LocalDateTime.of(2026, 1, 9, 21, 0)
        );

        LocalDateTime resolvedAt = LocalDateTime.of(2026, 1, 9, 22, 0);
        alert.resolve(resolvedAt);

        assertThat(alert.getStatus()).isEqualTo(AlertStatus.RESOLVED);
        assertThat(alert.getResolvedAt()).isEqualTo(resolvedAt);
    }
}
