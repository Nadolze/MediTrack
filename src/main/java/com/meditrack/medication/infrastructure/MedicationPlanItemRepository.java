package com.meditrack.medication.infrastructure;

import com.meditrack.medication.domain.MedicationPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository für einzelne Einträge eines Medikationsplans
 * (konkrete Einnahmen, Dosierungen, Zeiten).
 */
@Repository
public interface MedicationPlanItemRepository extends JpaRepository<MedicationPlanItem, String> {

    /**
     * Alle Einträge zu einem Plan, chronologisch aufsteigend.
     *
     * @param planId ID des Medikationsplans
     * @return Liste der Plan-Items
     */
    List<MedicationPlanItem> findByPlanIdOrderByCreatedAtAsc(String planId);
}
