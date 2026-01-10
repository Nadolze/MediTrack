package com.meditrack.alerts.domain.repository;

import com.meditrack.alerts.domain.entity.Alert;
import com.meditrack.alerts.domain.valueobject.AlertId;
import com.meditrack.alerts.domain.valueobject.AlertMessage;
import com.meditrack.alerts.domain.valueobject.PatientId;
import com.meditrack.alerts.domain.valueobject.Severity;
import com.meditrack.alerts.domain.valueobject.VitalReadingId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AlertRepositoryTest {

    @Autowired
    AlertRepository repository;

    @Test
    void existsByVitalReadingIdWorks() {
        Alert alert = Alert.trigger(
                AlertId.newId(),
                new PatientId("p1"),
                new VitalReadingId("r1"),
                Severity.CRITICAL,
                new AlertMessage("KRITISCH: ..."),
                LocalDateTime.now()
        );

        repository.save(alert);

        assertThat(repository.existsByVitalReadingId("r1")).isTrue();
        assertThat(repository.existsByVitalReadingId("r2")).isFalse();
    }

    @Test
    void findsAlertsByPatientOrderedDesc() {
        Alert a1 = Alert.trigger(
                new AlertId("a1"),
                new PatientId("p1"),
                new VitalReadingId("r1"),
                Severity.WARNING,
                new AlertMessage("Warnung"),
                LocalDateTime.of(2026, 1, 1, 10, 0)
        );

        Alert a2 = Alert.trigger(
                new AlertId("a2"),
                new PatientId("p1"),
                new VitalReadingId("r2"),
                Severity.CRITICAL,
                new AlertMessage("Kritisch"),
                LocalDateTime.of(2026, 1, 1, 12, 0)
        );

        repository.save(a1);
        repository.save(a2);

        List<Alert> result = repository.findByPatientIdOrderByCreatedAtDesc("p1");
        assertThat(result).hasSize(2);

        // getVitalReadingId() ist bei dir jetzt String → kein .value()
        assertThat(result.get(0).getVitalReadingId()).isEqualTo("r2");
    }
}
