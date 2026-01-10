package com.meditrack.vitals.domain.entity;

import com.meditrack.vitals.domain.valueobject.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class VitalReadingTest {

    @Test
    void createGeneratesIdAndStoresFields() {
        VitalReading r = VitalReading.create(
                new PatientId("patient-1"),
                VitalType.PULSE,
                new MeasurementValue(72),
                Unit.BPM,
                LocalDateTime.of(2026, 1, 1, 12, 0),
                "staff-1"
        );

        assertThat(r.getVitalReadingId().value()).isNotBlank();
        assertThat(r.getPatientId().value()).isEqualTo("patient-1");
        assertThat(r.getType()).isEqualTo(VitalType.PULSE);
        assertThat(r.getValue()).isEqualTo(72);
        assertThat(r.getUnit()).isEqualTo(Unit.BPM);
        assertThat(r.getRecordedByStaffId()).isEqualTo("staff-1");
    }

    @Test
    void measurementValueRejectsNaN() {
        assertThatThrownBy(() -> new MeasurementValue(Double.NaN))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void patientIdRejectsBlank() {
        assertThatThrownBy(() -> new PatientId(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
