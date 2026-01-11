package com.meditrack.vitals.application.service;

import com.meditrack.vitals.application.dto.CreateVitalReadingCommand;
import com.meditrack.vitals.application.dto.VitalReadingSummaryDto;
import com.meditrack.vitals.domain.entity.VitalReading;
import com.meditrack.vitals.domain.events.VitalReadingCreatedEvent;
import com.meditrack.vitals.domain.repository.VitalReadingRepository;
import com.meditrack.vitals.domain.valueobject.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Application Service für vitals.
 *
 * Verantwortlich für:
 * - Use-Cases (anlegen / listen)
 * - Publizieren des Domain Events
 */
@Service
public class VitalReadingService {

    // Repository für Persistenz von VitalReading-Aggregaten
    private final VitalReadingRepository repository;

    // Spring Event Publisher für Domain Events
    private final ApplicationEventPublisher eventPublisher;

    public VitalReadingService(VitalReadingRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Liefert eine Liste von Vitalwerten für einen Patienten.
     *
     */
    public List<VitalReadingSummaryDto> listForPatient(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return List.of();
        }
        return repository.findByPatientIdOrderByMeasuredAtDesc(patientId.trim())
                .stream()
                .map(v -> new VitalReadingSummaryDto(
                        v.getVitalReadingId().value(),
                        v.getPatientId().value(),
                        v.getType(),
                        v.getValue(),
                        v.getUnit(),
                        v.getMeasuredAt()
                ))
                .toList();
    }


    /**
     * Erstellt einen neuen Vitalwert.
     *
     */
    @Transactional
    public VitalReadingId create(CreateVitalReadingCommand command, String recordedByStaffId) {
        Objects.requireNonNull(command, "command darf nicht null sein.");

        // Pflichtfelder validieren und aufbereiten
        String patientId = requireText(command.getPatientId(), "patientId");
        VitalType type = Objects.requireNonNull(command.getType(), "type darf nicht null sein.");
        Double value = Objects.requireNonNull(command.getValue(), "value darf nicht null sein.");
        Unit unit = Objects.requireNonNull(command.getUnit(), "unit darf nicht null sein.");

        LocalDateTime measuredAt = (command.getMeasuredAt() == null) ? LocalDateTime.now() : command.getMeasuredAt();

        // Erzeugen des Domain-Aggregats
        VitalReading reading = VitalReading.create(
                new PatientId(patientId),
                type,
                new MeasurementValue(value),
                unit,
                measuredAt,
                recordedByStaffId
        );

        repository.save(reading);

        eventPublisher.publishEvent(new VitalReadingCreatedEvent(
                reading.getPatientId().value(),
                reading.getVitalReadingId().value(),
                reading.getType(),
                reading.getValue(),
                reading.getUnit()
        ));

        return reading.getVitalReadingId();
    }

    // Hilfsmethode zur Validierung von Textfeldern.
    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " darf nicht leer sein.");
        }
        return value.trim();
    }
}
