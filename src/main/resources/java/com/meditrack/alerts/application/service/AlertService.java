package com.meditrack.alerts.application.service;

import com.meditrack.alerts.application.dto.AlertSummaryDto;
import com.meditrack.alerts.domain.entity.Alert;
import com.meditrack.alerts.domain.repository.AlertRepository;
import com.meditrack.alerts.domain.service.AlertEvaluator;
import com.meditrack.alerts.domain.valueobject.AlertId;
import com.meditrack.alerts.domain.valueobject.PatientId;
import com.meditrack.alerts.domain.valueobject.VitalReadingId;
import com.meditrack.vitals.domain.events.VitalReadingCreatedEvent;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertEvaluator evaluator;
    private final Clock clock;

    public AlertService(AlertRepository alertRepository, AlertEvaluator evaluator, Clock clock) {
        this.alertRepository = alertRepository;
        this.evaluator = evaluator;
        this.clock = clock;
    }

    public void handle(VitalReadingCreatedEvent event) {
        // Doppelte Alerts verhindern (z.B. bei erneutem Event)
        if (alertRepository.existsByVitalReadingId(event.vitalReadingId())) {
            return;
        }

        Optional<AlertEvaluator.EvaluationResult> evaluation =
                evaluator.evaluate(event.type(), event.value(), event.unit());

        if (evaluation.isEmpty()) {
            return;
        }

        AlertEvaluator.EvaluationResult result = evaluation.get();

        // ✅ WICHTIG: Reihenfolge gemäß Alert.trigger(Signatur)
        Alert alert = Alert.trigger(
                AlertId.newId(),
                new PatientId(event.patientId()),
                new VitalReadingId(event.vitalReadingId()),
                result.severity(),
                result.message(),
                LocalDateTime.now(clock)
        );

        alertRepository.save(alert);
    }

    public List<AlertSummaryDto> listForPatient(String patientId) {
        return alertRepository.findByPatientId(patientId)
                .stream()
                .map(AlertSummaryDto::from)
                .toList();
    }

    public void acknowledge(String alertId) {
        alertRepository.findById(alertId).ifPresent(alert -> {
            // je nach Entity: acknowledge() oder acknowledge(now)
            // -> wir verwenden hier die Variante ohne Parameter, die in deinem Stand existiert
            alert.acknowledge();
            alertRepository.save(alert);
        });
    }

    public void resolve(String alertId) {
        alertRepository.findById(alertId).ifPresent(alert -> {
            alert.resolve(LocalDateTime.now(clock));
            alertRepository.save(alert);
        });
    }
}
