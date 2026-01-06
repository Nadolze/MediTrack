package com.meditrack.medication.application;

import com.meditrack.medication.application.dto.CreateMedicationPlanCommand;
import com.meditrack.medication.domain.MedicationPlan;
import com.meditrack.medication.infrastructure.MedicationPlanRepository;
import com.meditrack.shared.exception.AccessDeniedException;
import com.meditrack.shared.valueobject.UserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class MedicationPlanServiceTest {

    private MedicationPlanRepository medicationPlanRepository;
    private MedicationPlanService service;

    @BeforeEach
    void setUp() {
        medicationPlanRepository = mock(MedicationPlanRepository.class);
        service = new MedicationPlanService(medicationPlanRepository);
    }

    @Test
    void createPlan_asStaff_shouldPersistPlanAndReturnId() {
        UserSession actor = new UserSession("u1", "Dr. Staff", "staff@example.com", "STAFF");

        CreateMedicationPlanCommand command = new CreateMedicationPlanCommand();
        command.setPatientId("patient-123");
        command.setName("Basis-Medikationsplan");
        command.setDescription("Standard Medikation");
        command.setStartDate(LocalDate.of(2026, 1, 1));
        command.setEndDate(LocalDate.of(2026, 12, 31));

        MedicationPlan saved = MedicationPlan.create(
                command.getPatientId(),
                command.getName(),
                command.getDescription(),
                command.getStartDate(),
                command.getEndDate()
        );

        when(medicationPlanRepository.save(any(MedicationPlan.class))).thenReturn(saved);

        String id = service.createPlan(actor, command);

        assertThat(id).isEqualTo(saved.getId());

        ArgumentCaptor<MedicationPlan> captor = ArgumentCaptor.forClass(MedicationPlan.class);
        verify(medicationPlanRepository, times(1)).save(captor.capture());

        MedicationPlan persisted = captor.getValue();
        assertThat(persisted.getPatientId()).isEqualTo("patient-123");
        assertThat(persisted.getName()).isEqualTo("Basis-Medikationsplan");
    }

    @Test
    void createPlan_asPatient_shouldThrowAccessDenied() {
        UserSession actor = new UserSession("u2", "Patient", "p@example.com", "PATIENT");

        CreateMedicationPlanCommand command = new CreateMedicationPlanCommand();
        command.setPatientId("patient-123");
        command.setName("Plan");

        assertThatThrownBy(() -> service.createPlan(actor, command))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(medicationPlanRepository);
    }

    @Test
    void createPlan_withoutLogin_shouldThrowAccessDenied() {
        CreateMedicationPlanCommand command = new CreateMedicationPlanCommand();
        command.setPatientId("patient-123");
        command.setName("Plan");

        assertThatThrownBy(() -> service.createPlan(null, command))
                .isInstanceOf(AccessDeniedException.class);

        verifyNoInteractions(medicationPlanRepository);
    }
}
