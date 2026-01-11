package com.meditrack.medication.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mt_medication_plan")
public class MedicationPlan {

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @Column(nullable = false, length = 64)
    private String patientId;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected MedicationPlan() {
        // JPA
    }

    public static MedicationPlan create(
            String patientId,
            String name,
            String description,
            LocalDate startDate,
            LocalDate endDate
    ) {
        MedicationPlan plan = new MedicationPlan();
        plan.id = UUID.randomUUID().toString();
        plan.patientId = patientId;
        plan.name = name;
        plan.description = description;
        plan.startDate = startDate;
        plan.endDate = endDate;
        plan.active = true;
        plan.createdAt = LocalDateTime.now();
        return plan;
    }

    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
