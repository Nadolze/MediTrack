package com.meditrack.vitals.domain.repository;

import com.meditrack.vitals.domain.entity.VitalReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VitalReadingRepository extends JpaRepository<VitalReading, String> {

    List<VitalReading> findByPatientIdOrderByMeasuredAtDesc(String patientId);
}
