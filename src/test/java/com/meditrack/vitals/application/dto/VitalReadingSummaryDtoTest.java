package com.meditrack.vitals.application.dto;

import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * Unit-Tests für VitalReadingSummaryDto.
 *
 * Ziel:
 * - Prüfen, dass alle Getter/Accessoren korrekt Werte liefern
 * - equals und hashCode funktionieren konsistent bei gleichen Werten
 * - equals liefert false, wenn irgendein Feld unterschiedlich ist
 * - toString enthält relevante Felder und Klassennamen
 */
class VitalReadingSummaryDtoTest {

    @Test
    void shouldExposeValuesViaAccessors() {
        LocalDateTime measuredAt = LocalDateTime.of(2026, 1, 10, 12, 0, 0);

        VitalReadingSummaryDto dto = new VitalReadingSummaryDto(
                "vr-1",
                "patient-1",
                VitalType.PULSE,
                72.5,
                Unit.BPM,
                measuredAt
        );

        assertThat(dto.id()).isEqualTo("vr-1");
        assertThat(dto.patientId()).isEqualTo("patient-1");
        assertThat(dto.type()).isEqualTo(VitalType.PULSE);
        assertThat(dto.value()).isEqualTo(72.5);
        assertThat(dto.unit()).isEqualTo(Unit.BPM);
        assertThat(dto.measuredAt()).isEqualTo(measuredAt);
    }

    @Test
    void equalsAndHashCode_shouldWorkForSameValues() {
        LocalDateTime measuredAt = LocalDateTime.of(2026, 1, 10, 8, 30, 0);

        VitalReadingSummaryDto a = new VitalReadingSummaryDto(
                "vr-1",
                "patient-1",
                VitalType.TEMPERATURE,
                38.2,
                Unit.CELSIUS,
                measuredAt
        );

        VitalReadingSummaryDto b = new VitalReadingSummaryDto(
                "vr-1",
                "patient-1",
                VitalType.TEMPERATURE,
                38.2,
                Unit.CELSIUS,
                measuredAt
        );

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void equals_shouldBeFalseWhenAnyValueDiffers() {
        LocalDateTime measuredAt = LocalDateTime.of(2026, 1, 10, 8, 30, 0);

        VitalReadingSummaryDto a = new VitalReadingSummaryDto(
                "vr-1",
                "patient-1",
                VitalType.OXYGEN_SATURATION,
                95.0,
                Unit.PERCENT,
                measuredAt
        );

        VitalReadingSummaryDto b = new VitalReadingSummaryDto(
                "vr-2", // different id
                "patient-1",
                VitalType.OXYGEN_SATURATION,
                95.0,
                Unit.PERCENT,
                measuredAt
        );

        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void toString_shouldContainTypeAndKeyFields() {
        LocalDateTime measuredAt = LocalDateTime.of(2026, 1, 10, 9, 15, 0);

        VitalReadingSummaryDto dto = new VitalReadingSummaryDto(
                "vr-99",
                "patient-42",
                VitalType.BLOOD_PRESSURE_SYSTOLIC,
                135.0,
                Unit.MMHG,
                measuredAt
        );

        String s = dto.toString();

        assertThat(s).contains("VitalReadingSummaryDto");
        assertThat(s).contains("id=vr-99");
        assertThat(s).contains("patientId=patient-42");
        assertThat(s).contains("type=BLOOD_PRESSURE_SYSTOLIC");
    }
}
