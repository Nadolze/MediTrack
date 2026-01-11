package com.meditrack.assignment.domain.entity;

import com.meditrack.assignment.domain.valueobject.*;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Assignment Aggregate Root.
 *
 * Fachlich: Zuordnung von medizinischem Personal (Staff) zu einem Patienten.
 */
@Entity
@Table(name = "mt_assignment")
public class Assignment {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "patient_id", length = 36, nullable = false)
    private String patientId;

    @Column(name = "staff_id", length = 36, nullable = false)
    private String staffId;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_role", length = 50, nullable = false)
    private AssignmentRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private AssignmentStatus status;

    @Column(name = "assigned_at", nullable = false)
    private LocalDateTime assignedAt;

    @Column(name = "assigned_by_user_id", length = 36)
    private String assignedByUserId;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @Column(name = "ended_by_user_id", length = 36)
    private String endedByUserId;

    protected Assignment() {
        // JPA
    }

    private Assignment(
            AssignmentId id,
            PatientId patientId,
            StaffId staffId,
            AssignmentRole role,
            LocalDateTime assignedAt,
            String assignedByUserId
    ) {
        this.id = Objects.requireNonNull(id, "id").value();
        this.patientId = Objects.requireNonNull(patientId, "patientId").value();
        this.staffId = Objects.requireNonNull(staffId, "staffId").value();
        this.role = Objects.requireNonNull(role, "role");
        this.assignedAt = Objects.requireNonNull(assignedAt, "assignedAt");
        this.assignedByUserId = (assignedByUserId == null || assignedByUserId.isBlank()) ? null : assignedByUserId;
        this.status = AssignmentStatus.ACTIVE;
        this.endedAt = null;
        this.endedByUserId = null;
    }

    public static Assignment assign(
            PatientId patientId,
            StaffId staffId,
            AssignmentRole role,
            LocalDateTime assignedAt,
            String assignedByUserId
    ) {
        return new Assignment(AssignmentId.newId(), patientId, staffId, role, assignedAt, assignedByUserId);
    }

    public void end(LocalDateTime endedAt, String endedByUserId) {
        if (this.status == AssignmentStatus.ENDED) {
            return; // idempotent
        }
        this.status = AssignmentStatus.ENDED;
        this.endedAt = Objects.requireNonNull(endedAt, "endedAt");
        this.endedByUserId = (endedByUserId == null || endedByUserId.isBlank()) ? null : endedByUserId;
    }

    public AssignmentId getAssignmentId() {
        return new AssignmentId(id);
    }

    public PatientId getPatientId() {
        return new PatientId(patientId);
    }

    public StaffId getStaffId() {
        return new StaffId(staffId);
    }

    public AssignmentRole getRole() {
        return role;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public String getAssignedByUserId() {
        return assignedByUserId;
    }

    public LocalDateTime getEndedAt() {
        return endedAt;
    }

    public String getEndedByUserId() {
        return endedByUserId;
    }
}
