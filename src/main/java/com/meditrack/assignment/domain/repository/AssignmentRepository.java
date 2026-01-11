package com.meditrack.assignment.domain.repository;

import com.meditrack.assignment.domain.entity.Assignment;
import com.meditrack.assignment.domain.valueobject.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, String> {

    boolean existsByPatientIdAndStaffIdAndStatus(String patientId, String staffId, AssignmentStatus status);

    List<Assignment> findByPatientIdAndStatusOrderByAssignedAtDesc(String patientId, AssignmentStatus status);

    List<Assignment> findByStaffIdAndStatusOrderByAssignedAtDesc(String staffId, AssignmentStatus status);
}
