package com.meditrack.vitals.application.service;

import com.meditrack.vitals.application.dto.CreateVitalReadingCommand;
import com.meditrack.vitals.domain.events.VitalReadingCreatedEvent;
import com.meditrack.vitals.domain.repository.VitalReadingRepository;
import com.meditrack.vitals.domain.valueobject.Unit;
import com.meditrack.vitals.domain.valueobject.VitalType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class VitalReadingServiceTest {

    @Test
    void createSavesReadingAndPublishesEvent() {
        VitalReadingRepository repo = mock(VitalReadingRepository.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        VitalReadingService service = new VitalReadingService(repo, publisher);

        CreateVitalReadingCommand cmd = new CreateVitalReadingCommand();
        cmd.setPatientId("patient-1");
        cmd.setType(VitalType.TEMPERATURE);
        cmd.setValue(39.0);
        cmd.setUnit(Unit.CELSIUS);

        service.create(cmd, "staff-1");

        verify(repo, times(1)).save(any());

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(publisher, times(1)).publishEvent(captor.capture());

        assertThat(captor.getValue()).isInstanceOf(VitalReadingCreatedEvent.class);
        VitalReadingCreatedEvent ev = (VitalReadingCreatedEvent) captor.getValue();

        assertThat(ev.patientId()).isEqualTo("patient-1");
        assertThat(ev.type()).isEqualTo(VitalType.TEMPERATURE);
        assertThat(ev.unit()).isEqualTo(Unit.CELSIUS);
        assertThat(ev.value()).isEqualTo(39.0);
        assertThat(ev.vitalReadingId()).isNotBlank();
    }

    @Test
    void createRejectsMissingPatientId() {
        VitalReadingService service = new VitalReadingService(mock(VitalReadingRepository.class), mock(ApplicationEventPublisher.class));

        CreateVitalReadingCommand cmd = new CreateVitalReadingCommand();
        cmd.setPatientId(" ");
        cmd.setType(VitalType.PULSE);
        cmd.setValue(70.0);
        cmd.setUnit(Unit.BPM);

        assertThatThrownBy(() -> service.create(cmd, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("patientId");
    }
}
