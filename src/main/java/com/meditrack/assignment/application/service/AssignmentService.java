package com.meditrack.assignment.application.service;

import com.meditrack.assignment.application.dto.AssignmentSummaryDto;
import com.meditrack.assignment.application.dto.CreateAssignmentCommand;
import com.meditrack.assignment.domain.entity.Assignment;
import com.meditrack.assignment.domain.repository.AssignmentRepository;
import com.meditrack.assignment.domain.valueobject.*;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Application Service für den Assignment-BC.
@Service
public class AssignmentService {

    // Repository für Zugriff auf Assignments (Aggregate)
    private final AssignmentRepository repository;

    // Clock für testbare Zeitpunkte
    private final Clock clock;

    public AssignmentService(AssignmentRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    /**
     * STAFF/ADMIN weist einem Patienten einen Staff zu.
     */
    public AssignmentSummaryDto assign(CreateAssignmentCommand cmd, String assignedByUserId) {
        Objects.requireNonNull(cmd, "cmd");
        if (cmd.patientId() == null || cmd.patientId().isBlank()) {
            throw new IllegalArgumentException("patientId darf nicht leer sein");
        }
        if (cmd.staffId() == null || cmd.staffId().isBlank()) {
            throw new IllegalArgumentException("staffId darf nicht leer sein");
        }
        AssignmentRole role = (cmd.role() == null) ? AssignmentRole.SUPPORT : cmd.role();

        // keine doppelte ACTIVE-Zuweisung
        boolean exists = repository.existsByPatientIdAndStaffIdAndStatus(
                cmd.patientId(),
                cmd.staffId(),
                AssignmentStatus.ACTIVE
        );
        if (exists) {
            throw new IllegalStateException("Diese Zuweisung existiert bereits (ACTIVE).");
        }

        // Erzeugung des Aggregates
        Assignment assignment = Assignment.assign(
                new PatientId(cmd.patientId()),
                new StaffId(cmd.staffId()),
                role,
                LocalDateTime.now(clock),
                assignedByUserId
        );

        repository.save(assignment);
        return AssignmentSummaryDto.from(assignment);
    }

    /**
     * Liefert alle aktiven Zuweisungen eines Patienten
     * (absteigend nach Erstellungsdatum).
     */
    public List<AssignmentSummaryDto> listActiveForPatient(String patientId) {
        if (patientId == null || patientId.isBlank()) {
            return List.of();
        }

        return repository.findByPatientIdAndStatusOrderByAssignedAtDesc(patientId, AssignmentStatus.ACTIVE)
                .stream()
                .map(AssignmentSummaryDto::from)
                .toList();
    }

    /**
     * Beendet eine bestehende Zuweisung.
     *
     * Fachliche Regel:
     * - Assignment muss existieren
     * - Endzeitpunkt wird über Clock gesetzt
     */
    public void end(String assignmentId, String endedByUserId) {
        if (assignmentId == null || assignmentId.isBlank()) {
            throw new IllegalArgumentException("assignmentId darf nicht leer sein");
        }

        Assignment assignment = repository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment nicht gefunden: " + assignmentId));

        assignment.end(LocalDateTime.now(clock), endedByUserId);
        repository.save(assignment);
    }
}
