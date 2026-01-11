package com.meditrack.assignment.domain.repository;

import com.meditrack.assignment.domain.entity.Assignment;
import com.meditrack.assignment.domain.valueobject.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrationstest für das AssignmentRepository (JPA).
 *
 * Ziel:
 * - Verifikation der Repository-Query
 *   findByPatientIdAndStatusOrderByAssignedAtDesc(...)
 *
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AssignmentRepositoryTest {

    @Autowired
    AssignmentRepository repository;

    @Test
    void findByPatientIdAndStatusOrderByAssignedAtDesc_returnsOnlyActiveAndSortedDesc() {
        String patientId = "p-1";

        LocalDateTime t1 = LocalDateTime.of(2026, 1, 10, 10, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 1, 11, 10, 0);
        LocalDateTime t3 = LocalDateTime.of(2026, 1, 12, 10, 0);

        Assignment a1 = Assignment.assign(
                new PatientId(patientId),
                new StaffId("s-1"),
                AssignmentRole.PRIMARY_CARE,
                t1,
                "admin"
        );

        Assignment a2 = Assignment.assign(
                new PatientId(patientId),
                new StaffId("s-2"),
                AssignmentRole.SUPPORT,
                t2,
                "admin"
        );

        Assignment ended = Assignment.assign(
                new PatientId(patientId),
                new StaffId("s-3"),
                AssignmentRole.SUPPORT,
                t3,
                "admin"
        );
        ended.end(t3.plusHours(1), "admin");

        repository.saveAll(List.of(a1, a2, ended));

        var result = repository.findByPatientIdAndStatusOrderByAssignedAtDesc(patientId, AssignmentStatus.ACTIVE);

        assertThat(result).hasSize(2);

        // Wichtig: getStaffId() liefert ein ValueObject -> daher .value()
        assertThat(result.get(0).getStaffId().value()).isEqualTo("s-2");
        assertThat(result.get(1).getStaffId().value()).isEqualTo("s-1");

        assertThat(result.get(0).getAssignedAt()).isAfter(result.get(1).getAssignedAt());
    }
}
