package com.meditrack.assignment.application.dto;

import com.meditrack.assignment.domain.valueobject.AssignmentRole;

/**
 * Eingabe-DTO für den Use-Case "Staff einem Patienten zuweisen".
 */
public record CreateAssignmentCommand(
        String patientId,
        String staffId,
        AssignmentRole role
) {
}
