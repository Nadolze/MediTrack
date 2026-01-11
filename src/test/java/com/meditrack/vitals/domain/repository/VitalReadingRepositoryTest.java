package com.meditrack.vitals.domain.repository;

import com.meditrack.vitals.domain.entity.VitalReading;
import com.meditrack.vitals.domain.valueobject.MeasurementValue;
import com.meditrack.vitals.domain.valueobject.PatientId;
import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Integrationstest für das JPA-Repository VitalReadingRepository.
 *
 * Ziel:
 * - Prüfen, dass VitalReadings für einen Patienten korrekt gefunden und nach
 *   measuredAt absteigend sortiert zurückgegeben werden.
 * - Verwendet eine In-Memory-H2-Datenbank über das "test"-Profil.
 */
@DataJpaTest
@ActiveProfiles("test")
class VitalReadingRepositoryTest {

    @Autowired
    VitalReadingRepository repository;

    @Test
    void findsReadingsByPatientOrderedByMeasuredAtDesc() {
        VitalReading r1 = VitalReading.create(new PatientId("p1"), VitalType.PULSE, new MeasurementValue(70), Unit.BPM,
                LocalDateTime.of(2026, 1, 1, 10, 0), null);
        VitalReading r2 = VitalReading.create(new PatientId("p1"), VitalType.PULSE, new MeasurementValue(80), Unit.BPM,
                LocalDateTime.of(2026, 1, 1, 12, 0), null);

        repository.save(r1);
        repository.save(r2);

        List<VitalReading> result = repository.findByPatientIdOrderByMeasuredAtDesc("p1");
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getValue()).isEqualTo(80);
        assertThat(result.get(1).getValue()).isEqualTo(70);
    }
}
