package com.meditrack.medication.application;

import com.meditrack.medication.application.dto.CreateMedicationPlanCommand;
import com.meditrack.medication.application.dto.MedicationPlanSummaryDto;
import com.meditrack.medication.domain.MedicationPlan;
import com.meditrack.medication.infrastructure.MedicationPlanRepository;
import com.meditrack.shared.exception.AccessDeniedException;
import com.meditrack.shared.valueobject.UserSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Anwendungsservice für den BC Medication (Use-Cases).
 */
@Service
@Transactional
public class MedicationPlanService {

    /**
     * Repository für den Zugriff auf MedicationPlan-Entitäten.
     * Kapselt alle Datenbankzugriffe.
     */
    private final MedicationPlanRepository medicationPlanRepository;

    public MedicationPlanService(MedicationPlanRepository medicationPlanRepository) {
        this.medicationPlanRepository = medicationPlanRepository;
    }

    /**
     * Use-Case: Alle Medikationspläne eines Patienten abrufen.
     */
    public List<MedicationPlanSummaryDto> getPlansForPatient(String patientId) {
        return medicationPlanRepository.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * Neuer Use-Case: Plan anlegen. Erfordert STAFF/ADMIN.
     */
    public String createPlan(UserSession actor, CreateMedicationPlanCommand command) {
        requireStaffOrAdmin(actor);

        MedicationPlan plan = MedicationPlan.create(
                command.getPatientId(),
                command.getName(),
                command.getDescription(),
                command.getStartDate(),
                command.getEndDate()
        );

        MedicationPlan saved = medicationPlanRepository.save(plan);
        return saved.getId();
    }

    /**
     * Prüft, ob der aktuelle Benutzer STAFF oder ADMIN ist.
     */
    private void requireStaffOrAdmin(UserSession actor) {
        if (actor == null) {
            throw new AccessDeniedException("Bitte anmelden.");
        }
        if (!actor.hasAnyRole("ADMIN", "STAFF")) {
            throw new AccessDeniedException("Keine Berechtigung: Nur STAFF/ADMIN darf Medikationspläne anlegen.");
        }
    }
    /**
     * Wandelt ein Domain-Objekt (MedicationPlan) in ein Summary-DTO um.
     * Dadurch wird die Domain von der UI entkoppelt und die Datenmenge
     * für Listenansichten reduziert.
     */
    private MedicationPlanSummaryDto toSummary(MedicationPlan plan) {
        return MedicationPlanSummaryDto.builder()
                .id(plan.getId())
                .patientId(plan.getPatientId())
                .name(plan.getName())
                .active(plan.isActive())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .build();
    }
}
