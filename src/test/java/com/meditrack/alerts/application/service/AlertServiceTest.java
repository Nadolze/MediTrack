package com.meditrack.alerts.application.service;

import com.meditrack.alerts.domain.entity.Alert;
import com.meditrack.alerts.domain.repository.AlertRepository;
import com.meditrack.alerts.domain.service.AlertEvaluator;
import com.meditrack.alerts.domain.valueobject.AlertMessage;
import com.meditrack.alerts.domain.valueobject.Severity;
import com.meditrack.vitals.domain.events.VitalReadingCreatedEvent;
import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit-Test für AlertService.
 *
 * Ziel:
 * - Sicherstellen, dass Alerts nur unter korrekten Bedingungen erzeugt werden
 * - Verhindern von Duplikaten und unnötigen Persistierungen
 */
class AlertServiceTest {

    @Test
    void doesNotCreateAlertWhenAlreadyExistsForVitalReading() {
        AlertRepository repo = mock(AlertRepository.class);
        AlertEvaluator evaluator = mock(AlertEvaluator.class);

        when(repo.existsByVitalReadingId("r1")).thenReturn(true);

        AlertService service = new AlertService(repo, evaluator, fixedClock());

        service.handle(new VitalReadingCreatedEvent("p1", "r1", VitalType.PULSE, 70.0, Unit.BPM));

        verify(repo, never()).save(any(Alert.class));
        verify(evaluator, never()).evaluate(any(), anyDouble(), any());
    }

    @Test
    void doesNotCreateAlertWhenEvaluatorReturnsEmpty() {
        AlertRepository repo = mock(AlertRepository.class);
        AlertEvaluator evaluator = mock(AlertEvaluator.class);

        when(repo.existsByVitalReadingId("r1")).thenReturn(false);
        when(evaluator.evaluate(VitalType.PULSE, 70.0, Unit.BPM)).thenReturn(Optional.empty());

        AlertService service = new AlertService(repo, evaluator, fixedClock());

        service.handle(new VitalReadingCreatedEvent("p1", "r1", VitalType.PULSE, 70.0, Unit.BPM));

        verify(repo, never()).save(any(Alert.class));
    }

    @Test
    void createsAlertWhenEvaluatorReturnsResult() {
        AlertRepository repo = mock(AlertRepository.class);
        AlertEvaluator evaluator = mock(AlertEvaluator.class);

        when(repo.existsByVitalReadingId("r1")).thenReturn(false);

        AlertEvaluator.EvaluationResult result =
                new AlertEvaluator.EvaluationResult(
                        Severity.CRITICAL,
                        new AlertMessage("KRITISCH: PULSE = 200.0 bpm")
                );

        when(evaluator.evaluate(VitalType.PULSE, 200.0, Unit.BPM)).thenReturn(Optional.of(result));

        AlertService service = new AlertService(repo, evaluator, fixedClock());

        service.handle(new VitalReadingCreatedEvent("p1", "r1", VitalType.PULSE, 200.0, Unit.BPM));

        verify(repo, times(1)).save(any(Alert.class));
    }

    private Clock fixedClock() {
        return Clock.fixed(Instant.parse("2026-01-01T12:00:00Z"), ZoneId.of("UTC"));
    }
}
