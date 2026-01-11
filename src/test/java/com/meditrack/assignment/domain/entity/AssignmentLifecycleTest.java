package com.meditrack.assignment.domain.entity;

import com.meditrack.assignment.domain.valueobject.AssignmentRole;
import com.meditrack.assignment.domain.valueobject.AssignmentStatus;
import com.meditrack.assignment.domain.valueobject.PatientId;
import com.meditrack.assignment.domain.valueobject.StaffId;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AssignmentLifecycleTest {

    @Test
    void assign_createsActiveAssignment() {
        Assignment a = Assignment.assign(
                new PatientId("p1"),
                new StaffId("s1"),
                AssignmentRole.PRIMARY_CARE,
                LocalDateTime.of(2026, 1, 10, 10, 0),
                "admin-1"
        );

        assertThat(a.getAssignmentId().value()).isNotBlank();
        assertThat(a.getStatus()).isEqualTo(AssignmentStatus.ACTIVE);
        assertThat(a.getEndedAt()).isNull();
        assertThat(a.getEndedByUserId()).isNull();
    }

    @Test
    void end_isIdempotent() {
        Assignment a = Assignment.assign(
                new PatientId("p1"),
                new StaffId("s1"),
                AssignmentRole.SUPPORT,
                LocalDateTime.of(2026, 1, 10, 10, 0),
                "admin-1"
        );

        LocalDateTime ended1 = LocalDateTime.of(2026, 1, 10, 11, 0);
        a.end(ended1, "admin-2");

        LocalDateTime ended2 = LocalDateTime.of(2026, 1, 10, 12, 0);
        a.end(ended2, "admin-3");

        assertThat(a.getStatus()).isEqualTo(AssignmentStatus.ENDED);
        assertThat(a.getEndedAt()).isEqualTo(ended1);
        assertThat(a.getEndedByUserId()).isEqualTo("admin-2");
    }
}
