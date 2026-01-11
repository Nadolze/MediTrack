package com.meditrack.assignment.domain.service;

import com.meditrack.assignment.application.dto.CreateAssignmentCommand;
import com.meditrack.assignment.application.service.AssignmentService;
import com.meditrack.assignment.domain.entity.Assignment;
import com.meditrack.assignment.domain.repository.AssignmentRepository;
import com.meditrack.assignment.domain.valueobject.AssignmentRole;
import com.meditrack.assignment.domain.valueobject.AssignmentStatus;
import com.meditrack.assignment.domain.valueobject.PatientId;
import com.meditrack.assignment.domain.valueobject.StaffId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
/**
 * Unit-Tests für den AssignmentService.
 *
 * Ziel:
 * - Absicherung der Service-Logik unabhängig von Persistence
 * - Korrektes Fehlerverhalten bei ungültigen Zuständen
 *
 */
class AssignmentServiceTest {

    private AssignmentRepository repository;
    private Clock clock;
    private AssignmentService service;

    @BeforeEach
    void setUp() {
        repository = mock(AssignmentRepository.class);
        clock = Clock.fixed(Instant.parse("2026-01-10T10:00:00Z"), ZoneOffset.UTC);
        service = new AssignmentService(repository, clock);
    }

    @Test
    void assign_whenAlreadyExists_shouldThrowIllegalState() {
        when(repository.existsByPatientIdAndStaffIdAndStatus(eq("p-1"), eq("s-1"), eq(AssignmentStatus.ACTIVE)))
                .thenReturn(true);

        CreateAssignmentCommand cmd = new CreateAssignmentCommand("p-1", "s-1", AssignmentRole.PRIMARY_CARE);

        assertThatThrownBy(() -> service.assign(cmd, "admin"))
                .isInstanceOf(IllegalStateException.class);

        verify(repository, never()).save(any(Assignment.class));
    }

    @Test
    void end_whenNotFound_shouldThrow() {
        when(repository.findById("does-not-exist")).thenReturn(Optional.empty());

        // Implementation wirft IllegalArgumentException (nicht IllegalStateException)
        assertThatThrownBy(() -> service.end("does-not-exist", "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Assignment nicht gefunden");

        verify(repository, never()).save(any(Assignment.class));
    }
}
