package com.meditrack.medication.infrastructure;

import com.meditrack.medication.domain.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository für einzelne Medikamente.
 *
 * Dient nur der Medikation, nicht der Plan-Logik.
 */
@Repository
public interface MedicationRepository extends JpaRepository<Medication, String> {

    /**
     * Sucht Medikamente, deren Name den übergebenen String enthält
     * (case-insensitive).
     *
     * @param namePart Teil des Medikamenten-Namens
     * @return Liste passender Medikamente
     */
    List<Medication> findByNameContainingIgnoreCase(String namePart);
}
