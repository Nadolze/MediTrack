package com.meditrack.alerts.domain.repository;

import com.meditrack.alerts.domain.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, String> {

    boolean existsByVitalReadingId(String vitalReadingId);

    List<Alert> findByPatientId(String patientId);

    // Für Tests/Use-Cases, die "neueste zuerst" brauchen
    List<Alert> findByPatientIdOrderByCreatedAtDesc(String patientId);
}
