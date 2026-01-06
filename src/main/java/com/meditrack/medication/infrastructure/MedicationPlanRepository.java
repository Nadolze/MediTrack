package com.meditrack.medication.infrastructure;

import com.meditrack.medication.domain.MedicationPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationPlanRepository extends JpaRepository<MedicationPlan, String> {

    List<MedicationPlan> findByPatientIdOrderByCreatedAtDesc(String patientId);
}
