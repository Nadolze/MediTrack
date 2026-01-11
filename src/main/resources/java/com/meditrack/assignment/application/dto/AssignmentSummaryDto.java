package com.meditrack.assignment.application.dto;

import com.meditrack.assignment.domain.entity.Assignment;
import com.meditrack.assignment.domain.valueobject.AssignmentRole;
import com.meditrack.assignment.domain.valueobject.AssignmentStatus;

import java.time.LocalDateTime;

/**
 * View-DTO für Listen/Ansichten.
 */
public record AssignmentSummaryDto(
        String id,
        String patientId,
        String staffId,
        AssignmentRole role,
        AssignmentStatus status,
        LocalDateTime assignedAt,
        LocalDateTime endedAt
) {

    public static AssignmentSummaryDto from(Assignment a) {
        return new AssignmentSummaryDto(
                a.getAssignmentId().value(),
                a.getPatientId().value(),
                a.getStaffId().value(),
                a.getRole(),
                a.getStatus(),
                a.getAssignedAt(),
                a.getEndedAt()
        );
    }
}
