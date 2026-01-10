package com.meditrack.alerts.integration;

import com.meditrack.alerts.domain.repository.AlertRepository;
import com.meditrack.alerts.domain.valueobject.AlertStatus;
import com.meditrack.alerts.domain.valueobject.Severity;
import com.meditrack.vitals.application.dto.CreateVitalReadingCommand;
import com.meditrack.vitals.application.service.VitalReadingService;
import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class VitalsToAlertsIntegrationTest {

    @Autowired
    VitalReadingService vitalReadingService;

    @Autowired
    AlertRepository alertRepository;

    @Test
    void createCriticalPulse_shouldCreateAlertInRepository() {
        String patientId = "patient-xyz";

        CreateVitalReadingCommand cmd = new CreateVitalReadingCommand();
        cmd.setPatientId(patientId);
        cmd.setType(VitalType.PULSE);
        cmd.setValue(200.0);
        cmd.setUnit(Unit.BPM);
        cmd.setMeasuredAt(LocalDateTime.of(2026, 1, 9, 21, 0));

        vitalReadingService.create(cmd, null);

        var alerts = alertRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        assertThat(alerts).hasSize(1);

        var alert = alerts.get(0);
        assertThat(alert.getSeverity()).isEqualTo(Severity.CRITICAL);
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.OPEN);
        assertThat(alert.getMessage()).contains("PULSE");
        assertThat(alert.getPatientId()).isEqualTo(patientId);
    }

    @Test
    void createNormalPulse_shouldNotCreateAlert() {
        String patientId = "patient-ok";

        CreateVitalReadingCommand cmd = new CreateVitalReadingCommand();
        cmd.setPatientId(patientId);
        cmd.setType(VitalType.PULSE);
        cmd.setValue(80.0);
        cmd.setUnit(Unit.BPM);
        cmd.setMeasuredAt(LocalDateTime.of(2026, 1, 9, 21, 0));

        vitalReadingService.create(cmd, null);

        var alerts = alertRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        assertThat(alerts).isEmpty();
    }
}
