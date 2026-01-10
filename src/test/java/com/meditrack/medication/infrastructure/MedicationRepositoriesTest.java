package com.meditrack.medication.infrastructure;

import com.meditrack.medication.domain.Medication;
import com.meditrack.medication.domain.MedicationPlan;
import com.meditrack.medication.domain.MedicationPlanItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository-Integrationstests (H2) für den Medication-BC.
 */
@DataJpaTest
@ActiveProfiles("test")
class MedicationRepositoriesTest {

    @Autowired
    MedicationRepository medicationRepository;

    @Autowired
    MedicationPlanRepository medicationPlanRepository;

    @Autowired
    MedicationPlanItemRepository medicationPlanItemRepository;

    @Test
    @DisplayName("MedicationRepository: findByNameContainingIgnoreCase findet Einträge")
    void medicationRepository_shouldFindByNameContainingIgnoreCase() {
        // create(name, dosageForm, strength, description)
        Medication med = Medication.create("Ibuprofen", "TABLETTE", "400mg", "Schmerzmittel");
        medicationRepository.save(med);

        List<Medication> found = medicationRepository.findByNameContainingIgnoreCase("IBUPRO");

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getName()).isEqualTo("Ibuprofen");
    }

    @Test
    @DisplayName("MedicationPlanRepository: Plans werden pro Patient nach createdAt DESC geliefert")
    void planRepository_shouldReturnPlansForPatientOrderedByCreatedAtDesc() throws Exception {
        MedicationPlan p1 = MedicationPlan.create(
                "patient-1",
                "Plan A",
                "desc",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
        // Für deterministisches Ordering im Test setzen wir createdAt gezielt.
        setCreatedAtViaReflection(p1, LocalDateTime.of(2026, 1, 1, 10, 0));
        medicationPlanRepository.save(p1);

        MedicationPlan p2 = MedicationPlan.create(
                "patient-1",
                "Plan B",
                "desc",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 2, 28)
        );
        setCreatedAtViaReflection(p2, LocalDateTime.of(2026, 1, 2, 10, 0));
        medicationPlanRepository.save(p2);

        List<MedicationPlan> plans = medicationPlanRepository.findByPatientIdOrderByCreatedAtDesc("patient-1");

        assertThat(plans).hasSize(2);
        assertThat(plans.get(0).getName()).isEqualTo("Plan B");
        assertThat(plans.get(1).getName()).isEqualTo("Plan A");
    }

    @Test
    @DisplayName("MedicationPlanItemRepository: Items werden nach createdAt ASC geliefert")
    void planItemRepository_shouldReturnItemsForPlanOrderedByCreatedAtAsc() {
        Medication med = Medication.create("Paracetamol", "TABLETTE", "500mg", "Fieber");
        medicationRepository.save(med);

        MedicationPlan plan = MedicationPlan.create(
                "patient-2",
                "Plan",
                "desc",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31)
        );
        medicationPlanRepository.save(plan);

        // MedicationPlanItem.create() nimmt NUR die Item-Felder (ohne planId/medicationId).
        MedicationPlanItem i1 = MedicationPlanItem.create("Paracetamol", "500", "mg", "1x", "morgens", "-");
        i1.setPlan(plan);
        i1.setMedication(med);
        i1.setCreatedAt(Instant.parse("2026-01-01T10:00:00Z"));
        medicationPlanItemRepository.save(i1);

        MedicationPlanItem i2 = MedicationPlanItem.create("Paracetamol", "500", "mg", "1x", "abends", "-");
        i2.setPlan(plan);
        i2.setMedication(med);
        i2.setCreatedAt(Instant.parse("2026-01-01T12:00:00Z"));
        medicationPlanItemRepository.save(i2);

        List<MedicationPlanItem> items = medicationPlanItemRepository.findByPlanIdOrderByCreatedAtAsc(plan.getId());

        assertThat(items).hasSize(2);
        assertThat(items.get(0).getTimeOfDay()).isEqualTo("morgens");
        assertThat(items.get(1).getTimeOfDay()).isEqualTo("abends");
    }

    private static void setCreatedAtViaReflection(MedicationPlan plan, LocalDateTime value) throws Exception {
        Field f = MedicationPlan.class.getDeclaredField("createdAt");
        f.setAccessible(true);
        f.set(plan, value);
    }
}
